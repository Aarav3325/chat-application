package com.aarav.chatapplication.data.repository

import com.aarav.chatapplication.domain.repository.ChatRepository
import com.aarav.chatapplication.utils.Result
import com.aarav.chatapplication.utils.Result.Error
import com.aarav.chatapplication.utils.Result.Success
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import jakarta.inject.Inject
import java.io.IOException

class ChatRepositoryImpl @Inject constructor(
    val firebaseDatabase: FirebaseDatabase,
    val firebaseAuth: FirebaseAuth
) : ChatRepository {

    val user_chats = firebaseDatabase.getReference("user_chats")

    override suspend fun createChat(receiverUserId: String): Result<Unit> =
        try {

            val currentUserId = firebaseAuth.currentUser?.uid

            currentUserId?.let {

                val chatId = listOf(currentUserId, receiverUserId).sorted()

                user_chats.child(it)
                    .child(chatId.first())
                    .setValue(true)


                user_chats.child(receiverUserId)
                    .child(chatId.last())
                    .setValue(true)

                Success(Unit)
            } ?: run {
                Error("Unable to retrieve user id")
            }
        } catch (e: IOException) {
            Error(
                e.message ?: "Failed ot load chats"
            )
        }

}