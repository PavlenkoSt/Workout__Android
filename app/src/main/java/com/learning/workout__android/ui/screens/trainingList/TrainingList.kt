package com.learning.workout__android.ui.screens.trainingList

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.models.TrainingDay
import com.learning.workout__android.data.models.TrainingDayWithExercises
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import java.time.LocalDate

@Composable
fun TrainingList(
    trainingDays: List<TrainingDayWithExercises>,
    onGoToTrainingDay: (date: String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            end = 8.dp,
            start = 8.dp,
            bottom = 12.dp,
            top = 12.dp
        )
    ) {
        items(trainingDays, key = { it.trainingDay.date }) {
            TrainingItem(it, onGoToTrainingDay = { onGoToTrainingDay(it.trainingDay.date) })
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun TrainingListPreview() {
    Workout__AndroidTheme {
        TrainingList(
            trainingDays = List(5, {
                TrainingDayWithExercises(
                    trainingDay = TrainingDay(
                        id = it.toLong(),
                        date = LocalDate.parse("2025-10-${it + 10}").toString()
                    ),
                    exercises = emptyList(),
                )
            }),
            onGoToTrainingDay = {}
        )
    }
}