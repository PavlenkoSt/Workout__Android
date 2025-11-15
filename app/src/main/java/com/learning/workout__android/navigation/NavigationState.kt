package com.learning.workout__android.navigation

import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class NavigationState(
    val navHostController: NavHostController
) {
    fun navigateTo(route: Screen) {
        navHostController.navigate(route) {
            popUpTo(navHostController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
fun rememberNavigationState(
    navStateController: NavHostController = rememberNavController()
): NavigationState {
    return remember {
        NavigationState(navStateController)
    }
}

private object NavDebounce {
    var lastPopTime: Long = 0L
}

// to prevent pop back calls duplications
fun NavController.safePopBackStack(debounceMs: Long = 400L): Boolean {
    val now = SystemClock.elapsedRealtime()
    if (now - NavDebounce.lastPopTime < debounceMs) {
        // too soon, ignore
        return false
    }
    NavDebounce.lastPopTime = now
    return this.popBackStack()
}