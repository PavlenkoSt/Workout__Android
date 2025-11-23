package com.learning.workout__android

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import com.learning.workout__android.navigation.Navigator
import com.learning.workout__android.navigation.rememberNavigationState
import com.learning.workout__android.ui.components.BottomNavBar
import com.learning.workout__android.ui.screens.goals.GoalsScreen
import com.learning.workout__android.ui.screens.preset.PresetScreen
import com.learning.workout__android.ui.screens.presets.PresetsScreen
import com.learning.workout__android.ui.screens.records.RecordsScreen
import com.learning.workout__android.ui.screens.training.TrainingScreen
import com.learning.workout__android.ui.screens.trainingList.TrainingListScreen

@Composable
fun App() {
    val navState = rememberNavigationState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        bottomBar = { BottomNavBar(navState) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Navigator(
            navState = navState,
            trainingScreen = {
                TrainingScreen(
                    modifier = Modifier.padding(paddingValues),
                    snackbarHostState = snackbarHostState
                )
            },
            goalsScreen = {
                GoalsScreen(
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
                    snackbarHostState = snackbarHostState
                )
            },
            recordsScreen = {
                RecordsScreen(
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                )
            },
            presetsScreen = {
                PresetsScreen(
                    modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                )
            },
            presetScreen = {
                PresetScreen(
                    modifier = Modifier.padding(paddingValues),
                    presetId = it,
                    snackbarHostState = snackbarHostState
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