package com.aarav.chatapplication.domain.repository

import com.aarav.chatapplication.domain.model.User
import com.aarav.chatapplication.utils.Result

interface UserRepository {
    suspend fun storeUserInfo(user: User): Result<Unit>
}