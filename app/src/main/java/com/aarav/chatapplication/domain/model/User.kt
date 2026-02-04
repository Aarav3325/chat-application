package com.aarav.chatapplication.domain.model

data class User(
    val uid: String? = "",
    val phoneNumber: String = "",
    val name: String? = null,
    val avatar: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)