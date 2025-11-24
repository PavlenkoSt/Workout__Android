package com.learning.workout__android.ui.screens.goals

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.LocalSnackbarHostState
import com.learning.workout__android.data.models.ExerciseUnits
import com.learning.workout__android.data.models.Goal
import com.learning.workout__android.ui.screens.records.GoalsHeader
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.LoadState
import com.learning.workout__android.viewModel.GoalsViewModel
import com.learning.workout__android.viewModel.forms.GoalFormSeed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
) {
    val vm: GoalsViewModel =
        viewModel(factory = GoalsViewModel.provideFactory(LocalContext.current))
    val ui by vm.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val localSnackbarHostState = LocalSnackbarHostState.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            GoalsHeader(
                filter = ui.filter,
                onFilterChange = { vm.setFilter(it) },
                summary = ui.summary
            )
            when (val state = ui.goals) {
                is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is LoadState.Success -> {
                    GoalsList(
                        goals = state.data,
                        filter = ui.filter,
                        onIncrementGoal = { goal -> vm.updateGoal(goal.copy(count = goal.count + 1)) },
                        onDecrementGoal = { goal -> vm.updateGoal(goal.copy(count = if (goal.count > 0) goal.count - 1 else 0)) },
                        onEditGoalClick = {
                            vm.setGoalToEdit(it)
                            showBottomSheet = true
                        },
                        onDeleteGoalClick = { vm.deleteGoal(it) },
                        onSaveGoalAsRecordClick = {
                            vm.saveGoalAsRecord(it, onResult = { status ->
                                coroutineScope.launch {
                                    localSnackbarHostState.showSnackbar(
                                        if (status) "Added to records successfully"
                                        else "This is less than current record"
                                    )
                                }
                            })
                        },
                        summary = ui.summary
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showBottomSheet = true },
            shape = ShapeDefaults.ExtraLarge,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(all = 16.dp)
                .height(50.dp)
                .width(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }

        if (showBottomSheet) {
            fun onHide() {
                coroutineScope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                }
            }

            ModalBottomSheet(
                onDismissRequest = {
                    vm.setGoalToEdit(null)
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                GoalForm(
                    onSubmit = {
                        onHide()

                        ui.goalToEdit?.let { sourceGoal ->
                            vm.updateGoal(
                                sourceGoal.copy(
                                    name = it.name,
                                    targetCount = it.targetCount,
                                    units = it.units
                                )
                            )
                            return@GoalForm
                        }

                        vm.createGoal(
                            Goal(
                                name = it.name,
                                targetCount = it.targetCount,
                                units = it.units,
                            )
                        )
                    },
                    seed = GoalFormSeed(
                        name = ui.goalToEdit?.name ?: "",
                        targetCount = if (ui.goalToEdit != null) ui.goalToEdit?.targetCount.toString() else "",
                        units = ui.goalToEdit?.units ?: ExerciseUnits.REPS
                    ),
                    isEditing = ui.goalToEdit != null
                )
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun GoalsScreenPreview() {
    Workout__AndroidTheme {
        GoalsScreen()
    }
}