package com.stanislav_pav.repstation

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.stanislav_pav.repstation.navigation.Navigator
import com.stanislav_pav.repstation.navigation.rememberNavigationState
import com.stanislav_pav.repstation.ui.components.BottomNavBar
import com.stanislav_pav.repstation.ui.screens.goals.GoalsScreen
import com.stanislav_pav.repstation.ui.screens.pro.ProScreen
import com.stanislav_pav.repstation.ui.screens.preset.PresetScreen
import com.stanislav_pav.repstation.ui.screens.presets.PresetsScreen
import com.stanislav_pav.repstation.ui.screens.records.RecordsScreen
import com.stanislav_pav.repstation.ui.screens.training.TrainingScreen
import com.stanislav_pav.repstation.ui.screens.trainingList.TrainingListScreen
import com.stanislav_pav.repstation.viewModel.MonetizationViewModel

val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("No SnackbarHostState provided")
}

@Composable
fun App() {
    val navState = rememberNavigationState()
    val snackbarHostState = remember { SnackbarHostState() }
    val monetizationViewModel: MonetizationViewModel =
        viewModel(factory = MonetizationViewModel.provideFactory())
    val monetizationState by monetizationViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { BottomNavBar(navState) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalSnackbarHostState provides snackbarHostState,
            LocalMonetizationState provides monetizationState
        ) {
            Navigator(
                navState = navState,
                trainingScreen = {
                    TrainingScreen(
                        modifier = Modifier.padding(paddingValues),
                    )
                },
                goalsScreen = {
                    GoalsScreen(
                        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
                    )
                },
                recordsScreen = {
                    RecordsScreen(
                        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                    )
                },
                proScreen = {
                    ProScreen(
                        modifier = Modifier.padding(paddingValues),
                        monetizationViewModel = monetizationViewModel
                    )
                },
                presetsScreen = {
                    PresetsScreen(
                        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
                    )
                },
                presetScreen = { presetId ->
                    PresetScreen(
                        modifier = Modifier.padding(paddingValues),
                        presetId = presetId,
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
}
