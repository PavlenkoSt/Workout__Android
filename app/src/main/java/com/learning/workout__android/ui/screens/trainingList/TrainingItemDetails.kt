package com.learning.workout__android.ui.screens.trainingList

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.data.models.TrainingExercise
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.TrainingDay
import com.learning.workout__android.data.models.TrainingDayWithExercises
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatExerciseName
import java.time.LocalDate

@Composable
fun TrainingItemDetails(
    trainingDay: TrainingDayWithExercises,
    onGoToTrainingDay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)

    ) {
        if (trainingDay.sortedExercises.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "Training day is empty",
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                ExerciseItemsHeader()

                trainingDay.sortedExercises.forEach { ExerciseItem(it) }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onGoToTrainingDay, modifier = Modifier.align(Alignment.Center)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Go to training day", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ExerciseItemsHeader() {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 0.5.dp,
                    color = Color.LightGray
                )
        ) {
            Text(
                "Exercise name",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        TableCell("Reps", fontWeight = FontWeight.SemiBold)
        TableCell("Sets", fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ExerciseItem(exercise: TrainingExercise) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .weight(1f)
                .border(
                    width = 0.5.dp,
                    color = Color.LightGray
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(formatExerciseName(exercise), fontSize = 12.sp)

                Spacer(modifier = Modifier.width(4.dp))

                if (exercise.setsDone >= exercise.sets) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        tint = Color(0xFF14B8A6),
                        contentDescription = "Check",
                        modifier = Modifier
                            .size(16.dp)
                    )
                }
            }
        }

        TableCell("${exercise.reps}${if (exercise.type == ExerciseType.STATIC) "s" else ""}")
        TableCell("${exercise.setsDone}/${exercise.sets}")
    }
}

@Composable
private fun RowScope.TableCell(
    text: String,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Box(
        modifier = Modifier
            .width(70.dp)
            .border(
                width = 0.5.dp,
                color = Color.LightGray
            )
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = text, fontSize = 12.sp, fontWeight = fontWeight, modifier = Modifier.align(
                Alignment.Center
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun TrainingItemDetailsPreview() {
    Workout__AndroidTheme {
        TrainingItemDetails(
            TrainingDayWithExercises(
                trainingDay = TrainingDay(id = 0L, date = LocalDate.now().toString()),
                exercises = List(
                    5,
                    {
                        TrainingExercise(
                            it.toLong(),
                            0L,
                            "Pull ups",
                            10,
                            10,
                            10,
                            type = ExerciseType.DYNAMIC,
                            10,
                            1
                        )
                    })
            ),
            onGoToTrainingDay = {}
        )
    }
}