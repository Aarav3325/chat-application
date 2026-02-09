package com.aarav.chatapplication.presentation.navigation

sealed class NavRoute(val path: String) {
    object Home: NavRoute("home")
    object Chat: NavRoute("chat") {
        fun createRoute(receiverId: String, userId: String): String {
            return "chat/$receiverId/$userId"
        }
    }
    object Profile: NavRoute("profile")
    object Auth: NavRoute("auth")
}