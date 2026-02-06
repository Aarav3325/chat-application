package com.aarav.chatapplication.domain.repository

import com.aarav.chatapplication.data.model.ChatMeta
import kotlinx.coroutines.flow.Flow

interface ChatListRepository {
    fun observeUserChats(userId: String): Flow<List<String>>

    fun observeChatMeta(chatId: String): Flow<Pair<String, Long>>

    fun observeUnread(userId: String, chatId: String): Flow<Int>
}