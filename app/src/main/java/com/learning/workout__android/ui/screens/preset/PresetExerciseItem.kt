package com.learning.workout__android.ui.screens.preset

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.PresetExercise
import com.learning.workout__android.ui.components.SwipeToDismissItemBackground
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatExerciseName

@Composable
fun PresetExerciseItem(
    exercise: PresetExercise,
    index: Int,
    onSwipeToEdit: () -> Unit,
    onSwipeToDelete: () -> Unit,
    draggableHandler: @Composable () -> Unit,
    isDragging: Boolean
) {
    val swipeToDismissBoxState = key(exercise) {
        rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    onSwipeToDelete()
                } else if (it == SwipeToDismissBoxValue.StartToEnd) {
                    onSwipeToEdit()
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

    Column(
        modifier = Modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    ) {
        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            modifier = Modifier.fillMaxWidth(),
            backgroundContent = {
                SwipeToDismissItemBackground(swipeToDismissBoxState.dismissDirection)
            }
        ) {
            Card(
                modifier = Modifier,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
                border = CardDefaults.outlinedCardBorder(enabled = true)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${index + 1}. ${formatExerciseName(exercise.name, exercise.type)}",
                        modifier = Modifier.weight(1f)
                    )
                    StatColumn(title = "Reps", value = exercise.reps.toString())
                    StatColumn(title = "Sets", value = exercise.sets.toString())
                    StatColumn(title = "Rest", value = exercise.rest.toString())
                    draggableHandler()
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun RowScope.StatColumn(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .weight(0.3f)
    ) {
        Text(title, fontSize = 12.sp)
        Text(value, fontSize = 12.sp)
    }
}

@Composable
@Preview(showBackground = true)
private fun PresetExerciseItemPreview() {
    Workout__AndroidTheme {
        PresetExerciseItem(
            PresetExercise(
                presetId = 0L,
                name = "Pull ups",
                rest = 60,
                sets = 4,
                reps = 12,
                order = 0,
                type = ExerciseType.DYNAMIC
            ),
            index = 0,
            onSwipeToEdit = {},
            onSwipeToDelete = {},
            draggableHandler = {},
            isDragging = false
        )
    }
}
