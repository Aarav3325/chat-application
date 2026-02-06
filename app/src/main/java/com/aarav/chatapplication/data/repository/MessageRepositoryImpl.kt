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

    override fun listenMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val ref = rootRef.child(FirebasePaths.messages(chatId))

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

    override suspend fun makeMessageDelivered(
        chatId: String,
        messageIds: List<String>
    ) {
        val updates = mutableMapOf<String, Any>()

        messageIds.forEach { id ->
            updates["chats/$chatId/messages/$id/status"] = MessageStatus.DELIVERED.name
        }

        rootRef.updateChildren(updates).await()
    }

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
}