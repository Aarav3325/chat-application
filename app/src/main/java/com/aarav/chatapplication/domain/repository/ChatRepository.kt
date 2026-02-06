package com.aarav.chatapplication.domain.repository

import com.aarav.chatapplication.utils.Result

interface ChatRepository {
    suspend fun createChat(receiverUserId: String): Result<Unit>
}