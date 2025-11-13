package com.learning.workout__android.navigation

import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object TrainingStack : Screen()

    @Serializable
    data object TrainingScreen : Screen()

    @Serializable
    data object TrainingListScreen : Screen()

    @Serializable
    data object GoalsScreen : Screen()

    @Serializable
    data object RecordsScreen : Screen()

    @Serializable
    data object PresetsScreen : Screen()
}

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

@Composable
fun Navigator(
    navState: NavigationState,
    trainingScreen: @Composable () -> Unit,
    trainingListScreen: @Composable () -> Unit,
    goalsScreen: @Composable () -> Unit,
    recordsScreen: @Composable () -> Unit,
    presetsScreen: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalNavController provides navState.navHostController) {
        NavHost(
            navController = navState.navHostController,
            startDestination = Screen.TrainingStack
        ) {
            navigation<Screen.TrainingStack>(Screen.TrainingScreen) {
                composable<Screen.TrainingScreen> { trainingScreen() }
                composable<Screen.TrainingListScreen> { trainingListScreen() }
            }

            composable<Screen.GoalsScreen> { goalsScreen() }
            composable<Screen.RecordsScreen> { recordsScreen() }
            composable<Screen.PresetsScreen> { presetsScreen() }
        }
    }
}

