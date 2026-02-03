package com.aarav.chatapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aarav.chatapplication.chat.ChatCard
import com.aarav.chatapplication.chat.ChatScreen
import com.aarav.chatapplication.home.HomeScreen

@Composable
fun NavGraph(navHostController: NavHostController, modifier: Modifier) {
    NavHost(
        navHostController,
        startDestination = NavRoute.Home.path
    ) {
        addHomeScreen(navHostController, this)
        addChatScreen(navHostController, this)
    }
}

fun addHomeScreen(navController: NavController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(
        route = NavRoute.Home.path
    ) {
        HomeScreen(
            navigateToChat = {
                navController.navigate(NavRoute.Chat.path)
            }
        )
    }
}

fun addChatScreen(navController: NavController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(
        route = NavRoute.Chat.path
    ) {
        ChatScreen(
            back = {
                navController.popBackStack()
            }
        )
    }
}