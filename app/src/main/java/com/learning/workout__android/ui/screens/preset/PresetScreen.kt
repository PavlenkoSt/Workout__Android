package com.learning.workout__android.ui.screens.preset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.ui.components.ExerciseForm.ExerciseEditingFields
import com.learning.workout__android.ui.components.ExerciseForm.ExerciseForm
import com.learning.workout__android.utils.LoadState
import com.learning.workout__android.viewModel.PresetViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetScreen(
    modifier: Modifier,
    presetId: Long
) {
    val coroutineScope = rememberCoroutineScope()

    val vm: PresetViewModel = viewModel(
        factory = PresetViewModel.provideFactory(LocalContext.current, presetId)
    )
    val ui by vm.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    fun onAddExerciseClick() {
        showBottomSheet = true
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = ui.preset) {
            is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is LoadState.Success -> {
                PresetHeader(preset = state.data.preset, canUse = state.data.exercises.isNotEmpty())

                if (state.data.exercises.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column {
                            Spacer(modifier = Modifier.height(32.dp))
                            Text(
                                text = "No exercises yet",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            PresetFooter({ onAddExerciseClick() })
                        }
                    }
                } else {
                    PresetExerciseList(
                        modifier = modifier,
                        exercises = state.data.sortedExercises,
                        footer = { PresetFooter(onAddExerciseClick = { onAddExerciseClick() }) },
                        onSwipeToEdit = {
                            vm.setExerciseToEdit(it)
                            showBottomSheet = true
                        },
                        onSwipeToDelete = {
                            vm.deleteExerciseFromPreset(it)
                        },
                        reorderExercises = { from, to ->
                            vm.reorderExercises(from, to)
                        }
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
                            vm.setExerciseToEdit(null)
                            showBottomSheet = false
                        },
                        sheetState = sheetState
                    ) {
                        ExerciseForm(
                            onDefaultExerciseSubmit = { result ->
                                onHide()
                                ui.exerciseToEdit?.let {
                                    val exerciseToUpdate = it.copy(
                                        type = result.type,
                                        name = result.name,
                                        reps = result.reps.toInt(),
                                        rest = result.rest.toInt(),
                                        sets = result.sets.toInt()
                                    )
                                    vm.setExerciseToEdit(null)
                                    vm.updateExerciseInPreset(exercise = exerciseToUpdate)

                                    return@ExerciseForm
                                }

                                vm.addDefaultExercise(result)
                            },
                            onSimpleExerciseSubmit = { result ->
                                onHide()
                                // edit
                                ui.exerciseToEdit?.let {
                                    val exerciseToUpdate = it.copy(
                                        type = result.type,
                                        name = "",
                                        rest = 0,
                                        reps = 1,
                                        sets = 1
                                    )
                                    vm.setExerciseToEdit(null)
                                    vm.updateExerciseInPreset(exercise = exerciseToUpdate)

                                    return@ExerciseForm
                                }
                                vm.addSimpleExercise(result)
                            },
                            onLadderExerciseSubmit = { result ->
                                onHide()
                                vm.addLadderExercise(result)
                            },
                            exerciseEditingFields = ui.exerciseToEdit?.let {
                                ExerciseEditingFields(
                                    name = it.name,
                                    reps = it.reps,
                                    sets = it.sets,
                                    rest = it.rest,
                                    type = it.type
                                )
                            }
                        )
                    }

                }
            }
        }
    }
}