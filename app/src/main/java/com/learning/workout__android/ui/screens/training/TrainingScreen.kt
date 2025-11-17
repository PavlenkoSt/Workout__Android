package com.learning.workout__android.ui.screens.training

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.learning.workout__android.navigation.LocalNavController
import com.learning.workout__android.navigation.SaveStateHandleEnum
import com.learning.workout__android.ui.components.Calendar.Calendar
import com.learning.workout__android.ui.components.ExerciseForm.ExerciseEditingFields
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.TrainingViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState?
) {
    val coroutineScope = rememberCoroutineScope()

    val vm: TrainingViewModel = viewModel(
        factory = TrainingViewModel.provideFactory(LocalContext.current)
    )
    val ui by vm.uiState.collectAsState()

    val localNavController = LocalNavController.current

    val trainingDayPicked = localNavController.currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow(SaveStateHandleEnum.TrainingDayDate, "")
        ?.collectAsState()

    val initialPage = Int.MAX_VALUE / 2
    val initialWeekStart = remember { LocalDate.now().with(DayOfWeek.MONDAY) }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })

    var exerciseModalVisible by remember { mutableStateOf(false) }
    var saveAsPresetModalVisible by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        val weeksFromStart = pagerState.currentPage - initialPage
        val start =
            LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(weeksFromStart.toLong())
        vm.onWeekVisible(start)
    }

    // handle a picking date from another screen
    LaunchedEffect(trainingDayPicked?.value) {
        if (trainingDayPicked?.value?.isNotEmpty() == true) {
            val dateStr = trainingDayPicked?.value
            if (!dateStr.isNullOrEmpty()) {
                val pickedDate = LocalDate.parse(dateStr)
                vm.onDateSelected(pickedDate)

                // Monday of picked week
                val pickedWeekStart = pickedDate.with(DayOfWeek.MONDAY)

                // Your base Monday (same as in pager + Today button)
                val baseWeekStart = initialWeekStart

                val deltaWeeks = ChronoUnit.WEEKS.between(
                    baseWeekStart,
                    pickedWeekStart
                ).toInt()

                val targetPage = initialPage + deltaWeeks

                pagerState.animateScrollToPage(targetPage)

                localNavController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set(SaveStateHandleEnum.TrainingDayDate, "")
            }
        }
    }

    if (ui.isLoading) {
        Box(modifier = modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            Column {
                Calendar(
                    modifier = Modifier.fillMaxWidth(),
                    onDateClick = { date -> vm.onDateSelected(date.date) },
                    pagerState = pagerState,
                    calendarUiModel = ui.calendar,
                    initialWeekStart = initialWeekStart,
                    initialPage = initialPage,
                    title = ui.title,
                    allTrainingDays = ui.allTrainingDays
                )
                AnimatedContent(
                    targetState = ui.selectedDate,
                    label = "ExerciseListDaySwitch",
                    transitionSpec = {
                        if (targetState.isAfter(initialState)) {
                            slideInHorizontally { width -> width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> width } + fadeOut()
                        } else if (targetState.isBefore(initialState)) {
                            slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                    slideOutHorizontally { width -> -width } + fadeOut()
                        } else {
                            fadeIn() togetherWith fadeOut()
                        }
                    },
                ) { date ->
                    ExerciseList(
                        exercisesList = ui.currentDay?.sortedExercises ?: emptyList(),
                        onReorder = { from, to -> vm.reorderExercises(from, to) },
                        footer = {
                            TrainingFooter(
                                text = if (ui.currentDay != null) {
                                    "+ Add exercise"
                                } else {
                                    "Create training"
                                },
                                onClick = { exerciseModalVisible = true },
                                statistics = ui.currentDayStatistics
                            )
                        },
                        header = {
                            TrainingHeader(
                                currentDate = date,
                                modifier = Modifier.fillMaxWidth(),
                                isTrainingDay = ui.currentDay != null,
                                onDeleteTrainingDay = {
                                    vm.deleteTrainingDay(date)
                                },
                                onSaveAsPresetClick = {
                                    saveAsPresetModalVisible = true
                                },
                                hasExercises = ui.currentDay?.sortedExercises?.isNotEmpty() ?: false
                            )
                        },
                        emptyMessage = {
                            if (ui.currentDay != null && ui.currentDay?.exercises?.isEmpty() == true) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 12.dp)
                                ) {
                                    Text(
                                        text = "No exercises yet",
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        },
                        onDeleteExercise = { vm.deleteExercise(it) },
                        onSwipeToEditExercise = {
                            vm.setExerciseToEdit(it.id)
                            exerciseModalVisible = true
                        },
                        onDecrementExercise = {
                            vm.updateExercise(
                                it.copy(
                                    setsDone = if (it.setsDone > 0) it.setsDone - 1 else 0
                                )
                            )
                        },
                        onIncrementExercise = {
                            vm.updateExercise(
                                it.copy(
                                    setsDone = it.setsDone + 1
                                )
                            )
                        },
                    )
                }
            }
            if (!ui.calendar.selectedDate.isToday) {
                TodayFloatBtn(
                    onClick = {
                        val monday = vm.scrollToToday()
                        // jump pager to that week
                        val base = LocalDate.now().with(DayOfWeek.MONDAY)
                        val deltaWeeks = ChronoUnit.WEEKS.between(
                            base, monday
                        ).toInt()
                        val target = initialPage + deltaWeeks
                        // animate
                        coroutineScope.launch { pagerState.animateScrollToPage(target) }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(all = 16.dp)
                        .height(45.dp)
                )
            }
        }
    }

    if (exerciseModalVisible) {
        ExerciseModal(
            onDismiss = {
                vm.setExerciseToEdit(null)
                exerciseModalVisible = false
            },
            onDefaultExerciseSubmit = { result ->
                ui.exerciseToEdit?.let {
                    val exerciseToUpdate = it.copy(
                        type = result.type,
                        name = result.name,
                        reps = result.reps.toInt(),
                        rest = result.rest.toInt(),
                        sets = result.sets.toInt()
                    )
                    vm.setExerciseToEdit(null)
                    vm.updateExercise(exercise = exerciseToUpdate)

                    return@ExerciseModal
                }

                vm.addDefaultExercise(result)
            },
            onSimpleExerciseSubmit = { result ->
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
                    vm.updateExercise(exercise = exerciseToUpdate)

                    return@ExerciseModal
                }
                vm.addSimpleExercise(result)
            },
            onLadderExerciseSubmit = { result ->
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

    if (saveAsPresetModalVisible) {
        SaveAsPresetModal(
            onDismiss = { saveAsPresetModalVisible = false },
            onSubmit = {
                vm.saveAsPreset(it)
                if (snackbarHostState != null) {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Training day saved as preset")
                    }
                }
            }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TrainingScreenPreview() {
    Workout__AndroidTheme {
        TrainingScreen(snackbarHostState = null)
    }
}
