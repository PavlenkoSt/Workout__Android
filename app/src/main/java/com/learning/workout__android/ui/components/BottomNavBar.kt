package com.learning.workout__android.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.learning.workout__android.navigation.NavigationScreen
import com.learning.workout__android.navigation.NavigationState

@Composable
fun BottomNavBar(navState: NavigationState) {
    BottomAppBar {
        val navBackStackEntry by navState.navHostController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        NavigationBar {
            botBarRoutes.forEach { botBarRoute ->
                NavigationBarItem(
                    icon = { Icon(botBarRoute.icon, contentDescription = null) },
                    label = { Text(botBarRoute.name) },
                    selected = currentDestination?.hierarchy?.any { it.route === botBarRoute.route }
                        ?: false,
                    onClick = {
                        navState.navigateTo(botBarRoute.route)
                    }
                )
            }
        }
    }
}

data class BotBarRoute(val name: String, val route: String, val icon: ImageVector)

val botBarRoutes = listOf(
    BotBarRoute("Training", NavigationScreen.Training.route, Icons.Filled.Home),
    BotBarRoute("Goals", NavigationScreen.Goals.route, Icons.Filled.Done),
    BotBarRoute("Records", NavigationScreen.Records.route, Icons.Filled.Star),
    BotBarRoute("Presets", NavigationScreen.Presets.route, Icons.Filled.Favorite),
)