package com.aarav.chatapplication.data.repository

import android.util.Log
import com.aarav.chatapplication.data.model.ChatMeta
import com.aarav.chatapplication.data.model.Message
import com.aarav.chatapplication.data.model.MessageStatus
import com.aarav.chatapplication.data.remote.FirebasePaths
import com.aarav.chatapplication.domain.repository.MessageRepository
import com.aarav.chatapplication.utils.Result
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MessageRepositoryImpl @Inject constructor(
    firebaseDatabase: FirebaseDatabase
) : MessageRepository {

    private val rootRef = firebaseDatabase.reference

    override suspend fun sendMessage(
        chatId: String,
        senderId: String,
        receiverId: String,
        text: String
    ): Result<Unit> {
        return try {
            val messageRef = rootRef.child(FirebasePaths.messages(chatId)).push()

            val messageId = messageRef.key ?: throw Exception("Message id is null")

            val timestamp = System.currentTimeMillis()

            val message = Message(
                messageId,
                senderId,
                text,
                timestamp,
                MessageStatus.SENT.name
            )

            Log.i("SEND", ChatMeta(text, timestamp).toString())

            /*
            * paths to be updated
            * 1. user_chats - user_chats/$senderId/$chatId & user_chats/$receiverId/$chatId to true in
            * order to fetch chat list
            * 2. message - updated message pool for the current chat
            * 3. chat_meta - for last timestamp and message in current chat
            * 4. unread - update unread count for receiver
            * */

            val updates = hashMapOf<String, Any>(
                "user_chats/$senderId/$chatId" to true,
                "user_chats/$receiverId/$chatId" to true,
                FirebasePaths.message(chatId, messageId) to message,
                FirebasePaths.chatMeta(chatId) to ChatMeta(text, timestamp),
                FirebasePaths.unread(receiverId, chatId) to ServerValue.increment(1)
            )

            rootRef.updateChildren(updates).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Failed to send message")
        }
    }

    // Retrieve all messages for current chat ordered by timestamp
    override fun listenMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val ref = rootRef.child(FirebasePaths.messages(chatId))

        Log.i("SEND", ref.toString())
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(Message::class.java)
                }.sortedBy { it.timestamp }


                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose {
            ref.removeEventListener(listener)
        }
    }

//    override suspend fun makeMessageDelivered(
//        chatId: String,
//        messageIds: List<String>
//    ) {
//        val updates = mutableMapOf<String, Any>()
//
//        messageIds.forEach { id ->
//            updates["chats/$chatId/messages/$id/status"] = MessageStatus.DELIVERED.name
//        }
//
//        rootRef.updateChildren(updates).await()
//    }

    // Mark messages as read when user open the chat
    override suspend fun makeMessageRead(
        chatId: String,
        userId: String,
        messageIds: List<String>
    ) {
        val updates = mutableMapOf<String, Any>()

        messageIds.forEach { id ->
            updates["chats/$chatId/messages/$id/status"] = MessageStatus.READ.name
        }

        updates[FirebasePaths.unread(userId, chatId)] = 0

        rootRef.updateChildren(updates).await()
    }

    // Mark messages as delivered when user opens app
    override suspend fun makeChatMessagesDelivered(
        chatId: String,
        receiverId: String
    ) {
        val ref = rootRef.child(FirebasePaths.messages(chatId))

        val snapshot = ref.get().await()

        val updates = mutableMapOf<String, Any>()

        snapshot.children.forEach {
            msg ->
            val senderId = msg.child("senderId").getValue(String::class.java)
            val status = msg.child("status").getValue(String::class.java)

            if(
                senderId != receiverId
                && status == MessageStatus.SENT.name
            ) {
                updates["chats/$chatId/messages/${msg.key}/status"] = MessageStatus.DELIVERED.name
            }
        }

        if(updates.isNotEmpty()) {
            rootRef.updateChildren(updates).await()
        }
    }

    /* check if chat already exists
       use case - show empty message when chat is not yet created
    */
    override fun isChatCreated(chatId: String, userId: String): Flow<Boolean> = callbackFlow {

        val ref = rootRef.child(FirebasePaths.userChats(userId))

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = snapshot.children.mapNotNull {
                    it.key
                }.toSet()

                val isCreated = chats.any {
                    it == chatId
                }

                Log.i("CHAT", "is created: $isCreated")

                trySend(isCreated)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)

        awaitClose { ref.removeEventListener(listener) }

    }
}