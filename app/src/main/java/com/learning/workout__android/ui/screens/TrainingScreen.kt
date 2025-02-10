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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatDate
import io.github.boguszpawlowski.composecalendar.SelectableWeekCalendar
import io.github.boguszpawlowski.composecalendar.rememberSelectableWeekCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import java.time.LocalDate

@Composable
fun TrainingScreen(modifier: Modifier = Modifier) {
    val calendarState = rememberSelectableWeekCalendarState(
        initialSelection = listOf(LocalDate.now()),
        initialSelectionMode = SelectionMode.Single,
        confirmSelectionChange = { list -> list.isNotEmpty() },
    )

    val currentDate = calendarState.selectionState.selection[0]
    val isToday = currentDate == LocalDate.now()

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            SelectableWeekCalendar(
                calendarState = calendarState,
                weekDaysScrollEnabled = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Workout session - ${formatDate(currentDate)}",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (!isToday) {
            TodayFloatBtn(
                onClick = {
                    calendarState.selectionState.selection = listOf(LocalDate.now())
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun TodayFloatBtn(
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
@Preview
fun TrainingScreenPreview() {
    Workout__AndroidTheme {
        TrainingScreen()
    }
}