package com.aarav.chatapplication.utils

import com.aarav.chatapplication.domain.model.User
import java.util.UUID

object UserData {

    val userList = listOf(
        User(
            uid = "lkwcgdykDwa8F7lgtYcmLo01tO83",
            phoneNumber = "9999999999",
            name = "Test",
            avatar = "0xFFE8DA5A"
        ),
        User(
            uid = UUID.randomUUID().toString(),
            phoneNumber = "95580305846",
            name = "Rahul",
            avatar = "0xFF009BE4"
        ),
        User(
            uid = UUID.randomUUID().toString(),
            phoneNumber = "9754587589",
            name = "Ram",
            avatar = "0xFFE8DA5A"
        ),
        User(
            uid = UUID.randomUUID().toString(),
            phoneNumber = "9425678951",
            name = "Hasti",
            avatar = "0xFF00DDC5"
        ),
        User(
            uid = UUID.randomUUID().toString(),
            phoneNumber = "7689614528",
            name = "Simran",
            avatar = "0xFF009BE4"
        ),
    )
}