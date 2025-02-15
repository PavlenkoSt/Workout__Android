package com.learning.workout__android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.data.TrainingDaysMockData
import com.learning.workout__android.model.ExerciseModel
import com.learning.workout__android.ui.components.Calendar
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.ui.viewmodel.CalendarViewModel
import com.learning.workout__android.ui.viewmodel.CalendarViewModelFactory
import com.learning.workout__android.utils.formatDate
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate

@Composable
fun TrainingScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    val initialPage = Int.MAX_VALUE / 2
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })

    val calendarViewModel: CalendarViewModel =
        viewModel(factory = CalendarViewModelFactory(pagerState, initialPage))

    val days by remember { mutableStateOf(TrainingDaysMockData) }
    val currentDay =
        days.find { it.date == calendarViewModel.calendarUiModel.selectedDate.date.toString() }

    LaunchedEffect(pagerState.currentPage) {
        calendarViewModel.selectDayInWeek()
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            Calendar(
                modifier = Modifier.fillMaxWidth(),
                onDateClick = { calendarViewModel.onDateClick(it) },
                pagerState = pagerState,
                calendarUiModel = calendarViewModel.calendarUiModel,
                initialPage = initialPage,
                initialWeekStart = calendarViewModel.initialWeekStart,
                title = "${calendarViewModel.currentMonth} ${calendarViewModel.calendarUiModel.selectedDate.date.year}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Header(
                currentDate = calendarViewModel.calendarUiModel.selectedDate.date,
                modifier = Modifier.fillMaxWidth()
            )

            if (currentDay != null) {
                TrainingExerciseList(currentDay.exercises)
            }
        }

        if (!calendarViewModel.calendarUiModel.selectedDate.isToday) {
            TodayFloatBtn(
                onClick = {
                    coroutineScope.launch {
                        calendarViewModel.scrollToToday()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun Header(
    currentDate: LocalDate,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = "Workout session - ${formatDate(currentDate)}",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TodayFloatBtn(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        shape = ShapeDefaults.Large,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = null,
                modifier = Modifier.rotate(90F)
            )
            Text(text = "Today")
        }
    }
}

@Composable
private fun TrainingExerciseList(
    exercisesList: List<ExerciseModel>
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Update the list
    }

    LazyColumn(state = lazyListState) {
        items(exercisesList, key = { it.id }) {
            ReorderableItem(reorderableLazyListState, key = it.id) { isDragging ->
                ExerciseItem(
                    exercise = it,
                    modifier = Modifier.draggableHandle()
                )
            }
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: ExerciseModel,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = exercise.exercise)
    }
}

@Composable
@Preview
fun TrainingScreenPreview() {
    Workout__AndroidTheme {
        TrainingScreen()
    }
}
