package com.aarav.chatapplication.presentation.navigation

import android.R.attr.type
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.aarav.chatapplication.presentation.auth.AuthScreen
import com.aarav.chatapplication.presentation.chat.ChatScreen
import com.aarav.chatapplication.domain.repository.AuthRepository
import com.aarav.chatapplication.presentation.home.HomeScreen
import com.aarav.chatapplication.presentation.profile.ProfileScreen
import com.aarav.chatapplication.utils.generateChatId

@Composable
fun NavGraph(
    navHostController: NavHostController,
    authRepository: AuthRepository,
    userId: String?,
    modifier: Modifier
) {

    val isLoggedIn = authRepository.isLoggedIn()

    NavHost(
        navHostController,
        startDestination = if(isLoggedIn) NavRoute.Home.path else NavRoute.Auth.path
    ) {
        addHomeScreen(navHostController, this)
        addChatScreen(navHostController, this, userId ?: "")
        addAuthScreen(navHostController, this)
        addProfileScreen(navHostController, this)
    }
}

fun addHomeScreen(navController: NavController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(
        route = NavRoute.Home.path,
    ) {


        HomeScreen(
            navigateToChat = {
                receiverId, userId ->

                navController.navigate(NavRoute.Chat.createRoute(receiverId, userId))
            },
            onLogout = {
                navController.navigate(NavRoute.Auth.path) {
                    popUpTo(NavRoute.Home.path) {
                        inclusive = true
                    }
                }
            },
            chatListViewModel = hiltViewModel()
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

fun addProfileScreen(navController: NavController, navGraphBuilder: NavGraphBuilder) {
    navGraphBuilder.composable(
        route = NavRoute.Profile.path
    ) {
        ProfileScreen()
    }
}

fun addChatScreen(navController: NavController, navGraphBuilder: NavGraphBuilder, userId: String) {
    navGraphBuilder.composable(
        route = NavRoute.Chat.path.plus("/{receiverId}/{userId}"),
        arguments =
            listOf(
                navArgument("receiverId") {
                    type = NavType.StringType
                },
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
    ) {

        val receiverId = it.arguments?.getString("receiverId").toString()
        val userId = it.arguments?.getString("userId").toString()
        Log.i("MYTAG", "rec: " + receiverId)
        Log.i("MYTAG", "my: " + userId)

//        ChatScreen(
//            back = {
//                navController.popBackStack()
//            },
//            chatId = "test_lkwcgdykDwa8F7lgtYcmLo01tO83_070379f1-2065-4ef3-ad5f-0e0c34e611d9",
//            myId = "lkwcgdykDwa8F7lgtYcmLo01tO83",
//            otherUserId = "070379f1-2065-4ef3-ad5f-0e0c34e611d9",
//            chatViewModel = hiltViewModel()
//        )

        val chatId = generateChatId(userId, receiverId)

        Log.i("MYTAG", "chat: " + chatId)

        ChatScreen(
            back = {
                navController.popBackStack()
            },
            chatId = chatId,
            otherUserId = receiverId,
            myId = userId,
            chatViewModel = hiltViewModel()
        )
    }
}