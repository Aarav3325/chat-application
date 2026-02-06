package com.aarav.chatapplication.presentation.model

data class ChatListItem(
    val chatId: String,
    val otherUserId: String,
    val otherUserName: String,
    val lastMessage: String,
    val lastTimestamp: Long,
    val unreadCount: Int,
    val isOnline: Boolean
)