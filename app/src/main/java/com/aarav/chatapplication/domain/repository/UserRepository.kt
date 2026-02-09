package com.aarav.chatapplication.domain.repository

import com.aarav.chatapplication.domain.model.User
import com.aarav.chatapplication.utils.Result
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun storeUserInfo(user: User): Result<Unit>

    suspend fun writeTestData()

    fun getUserList(): Flow<List<User>>

    fun findUserByUserId(userId: String): Flow<User>

    fun getCurrentUser(): Result<String?>
}