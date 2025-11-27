package com.learning.workout__android.ui.screens.training

import DrawingCheckmark
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.TrainingExercise
import com.learning.workout__android.ui.components.ExerciseAnimatedProgressBar
import com.learning.workout__android.ui.components.ExerciseCountUpdateBtn
import com.learning.workout__android.ui.components.SwipeToDismissItemBackground
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatExerciseName


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseItem(
    exercise: TrainingExercise,
    index: Int,
    draggableHandler: @Composable () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    isDragging: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val swipeToDismissBoxState = key(exercise) {
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
                SwipeToDismissItemBackground(swipeToDismissBoxState.dismissDirection)
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

@Composable
private fun ExerciseHeaderRow(
    exercise: TrainingExercise,
    draggableHandler: @Composable () -> Unit,
    index: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val withStats = exercise.type in setOf(
            ExerciseType.DYNAMIC,
            ExerciseType.STATIC,
            ExerciseType.LADDER
        )

        Text(
            text = "${index + 1}. ${formatExerciseName(exercise.name, exercise.type)}",
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                .weight(if (withStats) 0.6f else 1f),
            color = MaterialTheme.colorScheme.onSurface
        )

        if (withStats) {
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
    exercise: TrainingExercise,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExerciseCountUpdateBtn(onClick = onDecrement, text = "-")

        Spacer(modifier = Modifier.width(16.dp))

        Box {
            val isCheckMarkVisible = exercise.setsDone >= exercise.sets

            ExerciseAnimatedProgressBar(
                count = exercise.setsDone,
                targetCount = exercise.sets
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

        ExerciseCountUpdateBtn(onClick = onIncrement, text = "+")
    }
}


@Composable
private fun RowScope.ExerciseStatItem(
    stat: String,
    value: String
) {
    Column(
        modifier = Modifier
            .padding(top = 4.dp)
            .weight(0.15f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            stat,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            value,
            fontSize = 12.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun ExerciseItemPreview() {
    Workout__AndroidTheme {
        ExerciseItem(
            exercise = TrainingExercise(
                0,
                trainingDayId = 0,
                name = "Exercise preview Exercise preview Exercise preview Exercise preview",
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
