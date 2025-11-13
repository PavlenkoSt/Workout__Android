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
import com.learning.workout__android.navigation.NavigationState
import com.learning.workout__android.navigation.Screen

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
                    selected = currentDestination?.hierarchy?.any {
                        it.route?.contains(
                            botBarRoute.route::class.simpleName ?: ""
                        ) == true
                    } ?: false,
                    onClick = {
                        navState.navigateTo(botBarRoute.route)
                    })
            }
        }
    }
}

data class BotBarRoute(val name: String, val route: Screen, val icon: ImageVector)

val botBarRoutes = listOf(
    BotBarRoute("Training", Screen.TrainingStack, Icons.Filled.Home),
    BotBarRoute("Goals", Screen.GoalsScreen, Icons.Filled.Done),
    BotBarRoute("Records", Screen.RecordsScreen, Icons.Filled.Star),
    BotBarRoute("Presets", Screen.PresetsScreen, Icons.Filled.Favorite),
)