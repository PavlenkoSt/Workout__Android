package com.learning.workout__android.ui.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.ui.theme.Workout__AndroidTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseItem(
    exercise: Exercise,
    idx: Int,
    draggableHandler: @Composable () ->  Unit,
    onDelete: () -> Unit,
    onEdit: (exercise: Exercise) -> Unit
) {
    val swipeToDismissBoxState = key(exercise) {
        rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    onDelete()
                } else if (it == SwipeToDismissBoxValue.StartToEnd) {
                    onEdit(exercise)
                }
                it != SwipeToDismissBoxValue.StartToEnd
            },
            positionalThreshold = { totalDistance -> totalDistance * 0.3f },
        )
    }

    Column {
        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            modifier = Modifier.fillMaxWidth(),
            backgroundContent = {
                when (swipeToDismissBoxState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd  -> {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit exercise",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(ShapeDefaults.Medium)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 24.dp)
                                .wrapContentSize(Alignment.CenterStart)
                            ,
                            tint = Color.White
                        )
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove exercise",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(ShapeDefaults.Medium)
                                .background(Color.Red)
                                .padding(horizontal = 24.dp)
                                .wrapContentSize(Alignment.CenterEnd),
                            tint = Color.White
                        )
                    }
                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "${idx + 1}. ${getExerciseName(exercise)}",
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp
                        )
                    )
                    if(
                        exercise.type == ExerciseType.DYNAMIC ||
                        exercise.type == ExerciseType.STATIC ||
                        exercise.type == ExerciseType.LADDER
                    ) {
                        when(exercise.type) {
                            ExerciseType.DYNAMIC -> ExerciseStatItem("Reps:", exercise.reps.toString())
                            ExerciseType.LADDER -> ExerciseStatItem("Reps:", exercise.reps.toString())
                            ExerciseType.STATIC -> ExerciseStatItem("Hold:", "${exercise.reps} sec.")
                            else -> null
                        }
                        ExerciseStatItem("Sets:", exercise.sets.toString())
                        ExerciseStatItem("Rest:", "${exercise.rest} sec.")
                    }
                    draggableHandler()
                }
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = ShapeDefaults.Medium
                    ) {
                        Text("-")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.width(80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${exercise.setsDone}/${exercise.sets}")
                        LinearProgressIndicator(
                            progress = { ((exercise.setsDone / exercise.sets)).toFloat() },
                            modifier = Modifier.fillMaxWidth(),
                            trackColor = MaterialTheme.colorScheme.onPrimary,
                            color = MaterialTheme.colorScheme.primary,
                            strokeCap = StrokeCap.Round,
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = ShapeDefaults.Medium
                    ) {
                        Text("+")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ExerciseStatItem(
    stat: String,
    value: String
) {
    Column (
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(stat, fontSize = 12.sp, lineHeight = 14.sp)
        Text(value, fontSize = 12.sp, lineHeight = 14.sp)
    }
}

private fun getExerciseName(exercise: Exercise): String {
    return when(exercise.type) {
        ExerciseType.FLEXIBILITY_SESSION -> {
            "Flexibility session"
        }
        ExerciseType.HAND_BALANCE_SESSION -> {
            "Hand balance session"
        }
        ExerciseType.WARMUP -> {
            "Warmup"
        }
        else -> {
            exercise.name
        }
    }
}

@Composable
@Preview
fun ExerciseItemPreview() {
    Workout__AndroidTheme {
        ExerciseItem(
            exercise = Exercise(0, trainingDayId = 0, name = "Exercise preview", reps = 10, sets = 10, setsDone = 2, type = ExerciseType.DYNAMIC, order = 0, rest = 10),
            draggableHandler = {},
            idx = 0,
            onDelete = {},
            onEdit = {}
        )
    }
}
