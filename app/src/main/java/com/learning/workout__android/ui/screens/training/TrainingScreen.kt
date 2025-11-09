package com.learning.workout__android.ui.screens.training

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.learning.workout__android.ui.components.Calendar
import com.learning.workout__android.ui.components.ExerciseForm
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.TrainingViewModel
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    val vm: TrainingViewModel = viewModel(
        factory = TrainingViewModel.provideFactory(LocalContext.current)
    )
    val ui by vm.uiState.collectAsState()

    val initialPage = Int.MAX_VALUE / 2
    val initialWeekStart = remember { LocalDate.now().with(DayOfWeek.MONDAY) }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState.currentPage) {
        val weeksFromStart = pagerState.currentPage - initialPage
        val start =
            LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(weeksFromStart.toLong())
        vm.onWeekVisible(start)
    }

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
            )

            ExerciseList(
                exercisesList = ui.currentDay?.sortedExercises ?: emptyList(),
                onReorder = { from, to -> vm.reorderExercises(from, to) },
                footer = {
                    TrainingFooter(
                        text = if (ui.currentDay != null) { "+ Add exercise" } else { "Create training" },
                        onClick = { showBottomSheet = true })
                },
                header = {
                    TrainingHeader(
                        currentDate = ui.selectedDate,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                },
                onDeleteExercise={ vm.deleteExercise(it) },
                onSwipeToEditExercise= {
                    vm.setExerciseToEdit(it)
                    showBottomSheet = true
                }
            )
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
                    .padding(16.dp)
            )
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                vm.setExerciseToEdit(null)
                showBottomSheet = false
           },
            sheetState = sheetState
        ) {
            ExerciseForm(
                onDefaultExerciseSubmit = { result ->
                    showBottomSheet = false

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

                        return@ExerciseForm
                    }

                    vm.addDefaultExercise(result)
                },
                onSimpleExerciseSubmit = { result ->
                    showBottomSheet = false

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

                        return@ExerciseForm
                    }
                    vm.addSimpleExercise(result)
                },
                onLadderExerciseSubmit = { result ->
                    showBottomSheet = false
                    vm.addLadderExercise(result)
                },
                exerciseToEdit = ui.exerciseToEdit
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TrainingScreenPreview() {
    Workout__AndroidTheme {
        TrainingScreen()
    }
}
