package com.aarav.chatapplication.data.repository

import android.util.Log
import com.aarav.chatapplication.domain.repository.UserRepository
import com.aarav.chatapplication.domain.model.User
import com.aarav.chatapplication.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
): UserRepository {

    private val userReference = firebaseDatabase.getReference("users")

    override suspend fun storeUserInfo(user: User): Result<Unit> {
        return try {
            userReference.child(user.uid.orEmpty())
                .setValue(user)
                .await()

            Log.i("USER", "Successfully saved user data for ${user.uid}")
            Result.Success(Unit)
        }
        catch (e: IOException) {
            Result.Error(e.message ?: "Failed to store user data")
        }
    }
}