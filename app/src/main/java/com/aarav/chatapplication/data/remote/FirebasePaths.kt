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

    fun group(groupId: String) =
        "groups/$groupId"

    fun groupMembers(groupId: String) =
        "groups/$groupId/members"

    fun groupMessages(groupId: String) =
        "group_messages/$groupId"

    fun groupMessage(groupId: String, messageId: String) =
        "group_messages/$groupId/$messageId"

    fun groupMeta(groupId: String) =
        "group_meta/$groupId"

    fun userGroups(userId: String) =
        "user_groups/$userId"

    fun groupUnread(userId: String, groupId: String) =
        "group_unread/$userId/$groupId"

    fun groupTyping(groupId: String, userId: String) =
        "group_typing/$groupId/$userId"
}