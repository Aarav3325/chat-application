package com.aarav.chatapplication

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aarav.chatapplication.domain.repository.AuthRepository
import com.aarav.chatapplication.domain.repository.PresenceRepository
import com.aarav.chatapplication.presentation.navigation.BottomNavigation
import com.aarav.chatapplication.presentation.navigation.NavGraph
import com.aarav.chatapplication.presentation.navigation.NavRoute
import com.aarav.chatapplication.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var presenceRepository: PresenceRepository

    @Inject
    lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(View(applicationContext)) { v, insets ->
//            val systemBars =
//                insets.getInsets(WindowInsetsCompat.Type.systemBars())
//                    v.setPadding(
//                    systemBars.left,
//                    systemBars.top,
//                    systemBars.right,
//                    systemBars.bottom
//                )
//            insets
//        }

//        val controller = WindowInsetsControllerCompat(
//            window,
//            View(applicationContext)
//        )
//        controller.isAppearanceLightStatusBars = true
//
//        ViewCompat.setOnApplyWindowInsetsListener(View(applicationContext)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
//            val bottomInset = maxOf(systemBars.bottom, ime.bottom)
//            v.setPadding(systemBars.left, 0, systemBars.right, bottomInset)
//            insets
//        }


        var currentUserId: String? = firebaseAuth.currentUser?.uid


//
//        presenceRepository.setupPresence("")


        setContent {
            AppTheme {
                val navController = rememberNavController()


                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val navItems = listOf(
                    NavRoute.Home.path,
                    NavRoute.Profile.path,
                )

                LaunchedEffect(Unit) {
                    currentUserId = firebaseAuth.currentUser?.uid

                }

                val show = currentRoute in navItems

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(show) {
                            BottomNavigation(navController)
                        }
                    }
                ) { innerPadding ->


                    NavGraph(
                        navController,
                        authRepository,
                        currentUserId,
                        Modifier.padding(innerPadding),
                    )
                }
            }
        }
    }
}
