package com.learning.workout__android.ui.components.Calendar

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.dataSources.CalendarDataSource
import com.learning.workout__android.data.models.TrainingDayWithExercises
import com.learning.workout__android.navigation.LocalNavController
import com.learning.workout__android.navigation.Screen
import com.learning.workout__android.viewModel.CalendarUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    title: String,
    calendarUiModel: CalendarUiModel,
    pagerState: PagerState,
    initialPage: Int,
    initialWeekStart: LocalDate,
    onDateClick: (CalendarUiModel.Date) -> Unit,
    allTrainingDays: List<TrainingDayWithExercises>
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.animateContentSize()) {
        Header(
            title = title,
            onPrevClick = {
                coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            },
            onNextClick = {
                coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )

        HorizontalPager(
            state = pagerState,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapPositionalThreshold = 0.2f
            )
        ) { page ->
            val weekOffset = page - initialPage
            val weekStart = initialWeekStart.plusWeeks(weekOffset.toLong())

            // Build the row model for this page, highlighting the VM’s selected day
            val rowCalendar = CalendarDataSource.getData(
                startDate = weekStart,
                lastSelectedDate = calendarUiModel.selectedDate.date
            )

            DaysRow(
                data = rowCalendar,
                onDateClick = onDateClick,
                activeDate = calendarUiModel.selectedDate.date,
                allTrainingDays = allTrainingDays
            )
        }
    }
}

@Composable
private fun Header(
    title: String,
    modifier: Modifier = Modifier,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    val navController = LocalNavController.current

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                navController.navigate(Screen.TrainingListScreen)
            }
        ) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null)
        }

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineSmall
        )
        IconButton(onClick = onPrevClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous"
            )
        }
        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next"
            )
        }
    }
}

@Composable
fun DaysRow(
    data: CalendarUiModel,
    onDateClick: (CalendarUiModel.Date) -> Unit,
    activeDate: LocalDate,
    allTrainingDays: List<TrainingDayWithExercises>
) {
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        data.week.forEach { date ->
            val targetDay = allTrainingDays.find { it.trainingDay.date == date.date.toString() }
            CalendarDay(
                date = date,
                onClick = onDateClick,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 2.dp)
                    .weight(1f),
                isActive = activeDate == date.date,
                trainingDayStatus = targetDay?.status
            )
        }
    }
}
