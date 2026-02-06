package com.aarav.chatapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aarav.chatapplication.auth.AuthScreen
import com.aarav.chatapplication.chat.ChatCard
import com.aarav.chatapplication.chat.ChatScreen
import com.aarav.chatapplication.domain.repository.AuthRepository
import com.aarav.chatapplication.home.HomeScreen

@Composable
fun NavGraph(
    navHostController: NavHostController,
    authRepository: AuthRepository,
    modifier: Modifier
) {

    val isLoggedIn = authRepository.isLoggedIn()

    NavHost(
        navHostController,
        startDestination = if(isLoggedIn) NavRoute.Home.path else NavRoute.Auth.path
    ) {
        addHomeScreen(navHostController, this)
        addChatScreen(navHostController, this)
        addAuthScreen(navHostController, this)
    }
}

fun addHomeScreen(navController: NavController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(
        route = NavRoute.Home.path
    ) {
        HomeScreen(
            navigateToChat = {
                navController.navigate(NavRoute.Chat.path)
            },
            charViewModel = hiltViewModel()
        )
    }
}
fun addAuthScreen(navController: NavController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(
        route = NavRoute.Auth.path
    ) {
        AuthScreen(
            navigateToHome = {
                navController.navigate(NavRoute.Home.path)
            },
            viewModel = hiltViewModel()
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