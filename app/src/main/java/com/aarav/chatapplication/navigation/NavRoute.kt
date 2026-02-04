package com.aarav.chatapplication.navigation

sealed class NavRoute(val path: String) {
    object Home: NavRoute("home")
    object Chat: NavRoute("chat")
    object Profile: NavRoute("profile")
    object Auth: NavRoute("auth")
}