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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.CalendarDataSource
import com.learning.workout__android.data.TrainingDaysMockData
import com.learning.workout__android.model.CalendarUiModel
import com.learning.workout__android.model.ExerciseModel
import com.learning.workout__android.ui.components.Calendar
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatDate
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate

@Composable
fun TrainingScreen(modifier: Modifier = Modifier) {
    // TODO move to view model
    var calendarUiModel by remember { mutableStateOf(CalendarDataSource.getData(lastSelectedDate = CalendarDataSource.today)) }

    val days by remember { mutableStateOf(TrainingDaysMockData) }
    val currentDay = days.find { it.date == calendarUiModel.selectedDate.date.toString() }

    fun onPrevWeekClick(startDate: LocalDate) {
        val finalStartDate = startDate.minusDays(1)
        calendarUiModel = CalendarDataSource.getData(
            startDate = finalStartDate,
            lastSelectedDate = calendarUiModel.selectedDate.date
        )
    }

    fun onNextWeekClick(endDate: LocalDate) {
        val finalStartDate = endDate.plusDays(2)
        calendarUiModel = CalendarDataSource.getData(
            startDate = finalStartDate,
            lastSelectedDate = calendarUiModel.selectedDate.date
        )
    }

    fun onDateClick(date: CalendarUiModel.Date) {
        calendarUiModel = calendarUiModel.copy(
            selectedDate = date,
            visibleDates = calendarUiModel.visibleDates.map {
                it.copy(
                    isSelected = it.date.isEqual(date.date)
                )
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            Calendar(
                modifier = Modifier.fillMaxWidth(),
                calendarUiModel = calendarUiModel,
                onPrevWeekClick = ::onPrevWeekClick,
                onNextWeekClick = ::onNextWeekClick,
                onDateClick = ::onDateClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Header(
                currentDate = calendarUiModel.selectedDate.date,
                modifier = Modifier.fillMaxWidth()
            )

            if (currentDay != null) {
                TrainingExerciseList(currentDay.exercises)
            }
        }

        if (!calendarUiModel.selectedDate.isToday) {
            TodayFloatBtn(
                onClick = {
                    onDateClick(
                        CalendarUiModel.Date(
                            date = LocalDate.now(),
                            isSelected = true,
                            isToday = true
                        )
                    )
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