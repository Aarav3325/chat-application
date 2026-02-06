package com.aarav.chatapplication.domain.repository

import kotlinx.coroutines.flow.Flow

interface TypingRepository {

    fun observeTyping(chatId: String): Flow<Set<String>>
    suspend fun setTyping(chatId: String, userId: String)
    suspend fun clearTyping(chatId: String, userId: String)
}