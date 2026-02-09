package com.aarav.chatapplication.data.repository

import com.aarav.chatapplication.data.remote.FirebasePaths
import com.aarav.chatapplication.domain.repository.TypingRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TypingRepositoryImpl @Inject constructor(
    val firebaseDatabase: FirebaseDatabase
) : TypingRepository {

    val rootRef = firebaseDatabase.reference

    // observe if the other user is typing or not
    override fun observeTyping(chatId: String): Flow<Set<String>> = callbackFlow {
        val ref = rootRef.child("typing")
            .child(chatId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val typingUsers = snapshot.children.mapNotNull {
                    it.key
                }.toSet()

                trySend(typingUsers)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    // set status to typing
    override suspend fun setTyping(chatId: String, userId: String) {
        rootRef.child(FirebasePaths.typing(chatId, userId))
            .setValue(true).await()
    }

    // clear typing status
    override suspend fun clearTyping(chatId: String, userId: String) {
        rootRef.child(FirebasePaths.typing(chatId, userId))
            .removeValue().await()
    }
}