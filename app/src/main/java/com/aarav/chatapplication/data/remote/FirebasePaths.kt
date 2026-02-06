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

    fun typing(chatId: String, userId: String) =
        "typing/$chatId/$userId"

    fun presence(userId: String) =
        "presence/$userId"

    fun userChats(userId: String) =
        "user_chats/$userId"
}