package com.aarav.chatapplication.data.repository

import android.util.Log
import com.aarav.chatapplication.data.remote.FirebasePaths
import com.aarav.chatapplication.domain.repository.ChatListRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ChatListRepositoryImpl
@Inject constructor(
    val firebaseDatabase: FirebaseDatabase
) : ChatListRepository {

    val rootRef = firebaseDatabase.reference

    // Retrieve chat list
    override fun observeUserChats(userId: String): Flow<List<String>> = callbackFlow {
        val ref = rootRef.child(FirebasePaths.userChats(userId))

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatIds = snapshot.children.mapNotNull {
                    it.key
                }

                Log.i("CHAT", "repo cahtIds: " + chatIds.toString())
                trySend(chatIds)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun observeChatMeta(chatId: String): Flow<Pair<String, Long>> = callbackFlow {
        val ref = rootRef.child(FirebasePaths.chatMeta(chatId))

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastMessage = snapshot.child("lastMessage").getValue(String::class.java) ?: ""
                val lastTimestamp = snapshot.child("lastTimestamp").getValue(Long::class.java) ?: 0L

                trySend(lastMessage to lastTimestamp)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun observeUnread(
        userId: String,
        chatId: String
    ): Flow<Int> = callbackFlow {
        val ref = rootRef.child(FirebasePaths.unread(userId, chatId))

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                Log.i("UNREAD", snapshot.getValue(Int::class.java).toString())
//                val unread = snapshot.getValue<Int>(Int::class.java) ?: 0
//                trySend(unread)
                val unread = (snapshot.value as? Long)?.toInt() ?: 0
                trySend(unread)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}