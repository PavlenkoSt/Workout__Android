package com.learning.workout__android.ui.screens.trainingList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.navigation.LocalNavController
import com.learning.workout__android.navigation.SaveStateHandleEnum
import com.learning.workout__android.navigation.safePopBackStack
import com.learning.workout__android.utils.LoadState
import com.learning.workout__android.viewModel.TrainingListFilterEnum
import com.learning.workout__android.viewModel.TrainingListViewModel

@Composable
fun TrainingListScreen(modifier: Modifier) {
    val vm: TrainingListViewModel = viewModel(
        factory = TrainingListViewModel.provideFactory(LocalContext.current)
    )
    val ui by vm.uiState.collectAsState()

    val navController = LocalNavController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 0.dp)
    ) {
        TrainingListHeader(
            onBack = {
                navController.safePopBackStack()
            },
            filter = ui.filter,
            onFilterChange = { vm.setFilter(it) },
            summary = ui.summary
        )
        when (val state = ui.trainingDays) {
            is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is LoadState.Success -> {
                if (state.data.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = if (ui.filter == TrainingListFilterEnum.All) {
                                "No training days yet"
                            } else {
                                "No ${ui.filter.toString().lowercase()} training days"
                            },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                TrainingList(trainingDays = state.data, onGoToTrainingDay = {
                    val navBackStackEntry = navController.previousBackStackEntry
                    navBackStackEntry?.savedStateHandle?.set(
                        SaveStateHandleEnum.TrainingDayDate,
                        it
                    )
                    navController.safePopBackStack()
                })
            }
        }
    }
}