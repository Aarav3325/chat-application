package com.aarav.chatapplication.utils

fun generateChatId(userA: String, userB: String): String {
    return if (userA < userB) (userA + "_" +userB) else (userB + "_" + userA)
}