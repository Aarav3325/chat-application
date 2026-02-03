package com.aarav.chatapplication.navigation


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.aarav.chatapplication.ui.theme.manrope

@Composable
fun BottomNavigation(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val navItems = listOf(NavItem.Chat, NavItem.Profile)

    NavigationBar(
        tonalElevation = 4.dp,
        containerColor = Color(0xFF222222),
        modifier = Modifier.shadow(24.dp)
    ) {
        navItems.forEachIndexed { index, item ->

            val isSelected = currentRoute?.startsWith(item.path) == true
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.path) {
                        navController.navigate(item.path) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }

                    }
                    Log.i("NAV", "BottomNavigationBar: $currentRoute, dest : ${item.path}")
                },
                label = {
                    Text(
                        item.name,
                        fontFamily = manrope
                    )
                },
                icon = {
                    val icon = if (isSelected) item.filledIcon else item.icon

                    Image(
                        painter = painterResource(icon),
                        contentDescription = "nav icon",
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color(0xFF8F8F8F),
                )
            )
        }
    }
}