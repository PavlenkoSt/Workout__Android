package com.learning.workout__android

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.learning.workout__android.navigation.Navigator
import com.learning.workout__android.navigation.rememberNavigationState
import com.learning.workout__android.ui.components.BottomNavBar
import com.learning.workout__android.ui.screens.GoalsScreen
import com.learning.workout__android.ui.screens.presets.PresetsScreen
import com.learning.workout__android.ui.screens.RecordsScreen
import com.learning.workout__android.ui.screens.training.TrainingScreen
import com.learning.workout__android.ui.screens.trainingList.TrainingListScreen

@Composable
fun App() {
    val navState = rememberNavigationState()

    Scaffold(
        bottomBar = {
            BottomNavBar(navState)
        },
    ) { paddingValues ->
        Navigator(
            navState = navState,
            trainingScreen = {
                TrainingScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            },
            goalsScreen = {
                GoalsScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            },
            recordsScreen = {
                RecordsScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            },
            presetsScreen = {
                PresetsScreen(
                    modifier = Modifier.padding(paddingValues)
                )
            },
            trainingListScreen = {
                TrainingListScreen(
                    modifier = Modifier.padding(
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        bottom = paddingValues.calculateBottomPadding()
                    )
                )
            }
        )
    }
}