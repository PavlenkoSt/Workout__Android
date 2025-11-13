package com.learning.workout__android.ui.screens.training

import DrawingCheckmark
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
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
    index: Int,
    draggableHandler: @Composable () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isDragging: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val swipeToDismissBoxState = key(exercise.id) {
        rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    onDelete()
                } else if (it == SwipeToDismissBoxValue.StartToEnd) {
                    onEdit()
                }
                it != SwipeToDismissBoxValue.StartToEnd
            },
            positionalThreshold = { totalDistance -> totalDistance * 0.3f },
        )
    }

    val scale by animateFloatAsState(
        targetValue = if (isDragging) 1.05f else 1f,
        label = "scale"
    )

    Column(modifier = Modifier.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }) {
        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            modifier = Modifier.fillMaxWidth(),
            backgroundContent = {
                BackgroundContent(swipeToDismissBoxState.dismissDirection)
            }
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                border = CardDefaults.outlinedCardBorder(enabled = true)
            ) {
                Column {
                    ExerciseHeaderRow(
                        exercise = exercise,
                        draggableHandler = draggableHandler,
                        index = index
                    )
                    ExerciseSetsRow(
                        exercise = exercise,
                        onIncrement = onIncrement,
                        onDecrement = onDecrement
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BackgroundContent(dismissDirection: SwipeToDismissBoxValue) {
    when (dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit exercise",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(ShapeDefaults.Medium)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 24.dp)
                    .wrapContentSize(Alignment.CenterStart),
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

@Composable
private fun ExerciseHeaderRow(
    exercise: Exercise,
    draggableHandler: @Composable () -> Unit,
    index: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${index + 1}. ${getExerciseName(exercise)}",
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
            color = MaterialTheme.colorScheme.onSurface
        )

        if (exercise.type in setOf(
                ExerciseType.DYNAMIC,
                ExerciseType.STATIC,
                ExerciseType.LADDER
            )
        ) {
            when (exercise.type) {
                ExerciseType.DYNAMIC, ExerciseType.LADDER ->
                    ExerciseStatItem("Reps:", exercise.reps.toString())

                ExerciseType.STATIC ->
                    ExerciseStatItem("Hold:", "${exercise.reps} sec.")

                else -> {}
            }
            ExerciseStatItem("Sets:", exercise.sets.toString())
            ExerciseStatItem("Rest:", "${exercise.rest} sec.")
        }
        draggableHandler()
    }
}

@Composable
private fun ExerciseSetsRow(
    exercise: Exercise,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SetsUpdateBtn(onClick = onDecrement, text = "-")

        Spacer(modifier = Modifier.width(16.dp))

        Box {
            val isCheckMarkVisible = exercise.setsDone >= exercise.sets

            ExerciseAnimatedProgressBar(
                setsDone = exercise.setsDone,
                sets = exercise.sets
            )

            if (isCheckMarkVisible) {
                DrawingCheckmark(
                    isVisible = true,
                    size = 28,
                    modifier = Modifier
                        .align(alignment = Alignment.BottomEnd)
                        .offset(x = 8.dp, y = 4.dp),
                    backgroundColor = Color(0xFF14B8A6)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        SetsUpdateBtn(onClick = onIncrement, text = "+")
    }
}

// Keep existing helper composables but optimize DrawingCheckmark call
@Composable
private fun RowScope.SetsUpdateBtn(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f),
        shape = ShapeDefaults.Medium,
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        contentPadding = PaddingValues(vertical = 0.dp)
    ) {
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ExerciseAnimatedProgressBar(
    setsDone: Int,
    sets: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (setsDone.toFloat() / sets.toFloat()),
        label = "progress"
    )

    Spacer(modifier = Modifier.width(16.dp))
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${setsDone}/${sets}")
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surface,
            color = MaterialTheme.colorScheme.primary,
            strokeCap = StrokeCap.Round,
        )
    }
}

@Composable
private fun ExerciseStatItem(
    stat: String,
    value: String
) {
    Column(
        modifier = Modifier.padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            stat,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            value,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun getExerciseName(exercise: Exercise): String {
    return when (exercise.type) {
        ExerciseType.FLEXIBILITY_SESSION -> "Flexibility session"
        ExerciseType.HAND_BALANCE_SESSION -> "Hand balance session"
        ExerciseType.WARMUP -> "Warmup"
        else -> exercise.name
    }
}

@Composable
@Preview
fun ExerciseItemPreview() {
    Workout__AndroidTheme {
        ExerciseItem(
            exercise = Exercise(
                0,
                trainingDayId = 0,
                name = "Exercise preview",
                reps = 10,
                sets = 10,
                setsDone = 2,
                type = ExerciseType.DYNAMIC,
                order = 0,
                rest = 10
            ),
            draggableHandler = {},
            onDelete = {},
            onEdit = {},
            isDragging = false,
            onDecrement = {},
            onIncrement = {},
            index = 0
        )
    }
}
