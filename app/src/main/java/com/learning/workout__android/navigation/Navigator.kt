package com.learning.workout__android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun Navigator(
    navState: NavigationState,
    trainingScreen: @Composable () -> Unit,
    goalsScreen: @Composable () -> Unit,
    recordsScreen: @Composable () -> Unit,
    presetsScreen: @Composable () -> Unit,
) {

    NavHost(
        navController = navState.navHostController,
        startDestination = NavigationScreen.Training.route
    ) {
        composable(route = NavigationScreen.Training.route) { trainingScreen() }
        composable(route = NavigationScreen.Goals.route) { goalsScreen() }
        composable(route = NavigationScreen.Records.route) { recordsScreen() }
        composable(route = NavigationScreen.Presets.route) { presetsScreen() }
    }
}

sealed class NavigationScreen(val route: String) {
    data object Training : NavigationScreen(TRAINING_SCREEN)
    data object Goals : NavigationScreen(GOALS_SCREEN)
    data object Records : NavigationScreen(RECORDS_SCREEN)
    data object Presets : NavigationScreen(PRESETS_SCREEN)

    companion object {
        const val TRAINING_SCREEN = "training"
        const val GOALS_SCREEN = "goals"
        const val RECORDS_SCREEN = "records"
        const val PRESETS_SCREEN = "presets"
    }
}

