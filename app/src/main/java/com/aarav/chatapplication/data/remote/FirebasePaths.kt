package com.aarav.chatapplication.data.remote

object FirebasePaths {
    fun messages(chatId: String) =
        "chats/$chatId/messages"

    fun message(chatId: String, messageId: String) =
        "chats/$chatId/messages/$messageId"

    fun chatMeta(chatId: String) =
        "chat_meta/$chatId"

    fun unread(userId: String, chatId: String) =
        "unread/$userId/$chatId"
}