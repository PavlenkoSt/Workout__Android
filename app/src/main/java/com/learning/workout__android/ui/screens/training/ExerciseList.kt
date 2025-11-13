package com.learning.workout__android.ui.screens.training

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.R
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun ExerciseList(
    exercisesList: List<Exercise>,
    onReorder: (from: Exercise, to: Exercise) -> Unit,
    onDeleteExercise: (exercise: Exercise) -> Unit,
    onIncrementExercise: (exercise: Exercise) -> Unit,
    onDecrementExercise: (exercise: Exercise) -> Unit,
    onSwipeToEditExercise: (exercise: Exercise) -> Unit,
    footer: @Composable () -> Unit,
    header: @Composable () -> Unit,
    emptyMessage: @Composable () -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Adjust indices to account for header item (header is at index 0)
        val adjustedFrom = from.index - 1 // Subtract 1 for header
        val adjustedTo = to.index - 1 // Subtract 1 for header

        if (adjustedFrom in exercisesList.indices && adjustedTo in 0..exercisesList.size) {
            onReorder(exercisesList[adjustedFrom], exercisesList[adjustedTo])
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(end = 8.dp, start = 8.dp, bottom = 12.dp),
    ) {
        item(key = "header") {
            header()
            if(exercisesList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (exercisesList.isNotEmpty()) {
            val canReorder = exercisesList.size > 1

            items(items = exercisesList, key = { item -> item.id }) { item ->
                ReorderableItem(
                    reorderableLazyListState,
                    key = item.id,
                    enabled = canReorder
                ) { isDragging ->
                    ExerciseItem(
                        exercise = item,
                        draggableHandler = {
                            if (canReorder) {
                                DraggableHandler(
                                    modifier = Modifier
                                        .draggableHandle()
                                        .longPressDraggableHandle()
                                )
                            }
                        },
                        isDragging = isDragging,
                        onDelete = { onDeleteExercise(item) },
                        onEdit = { onSwipeToEditExercise(item) },
                        onIncrement = { onIncrementExercise(item) },
                        onDecrement = { onDecrementExercise(item) }
                    )
                }
            }
        } else {
            item("empty") {
                emptyMessage()
            }
        }

        item(key = "footer") {
            footer()
        }
    }
}

@Composable
private fun DraggableHandler(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.hand),
        contentDescription = "Handle",
        tint = Color.Gray.copy(alpha = 0.6f),
        modifier = modifier
            .padding(vertical = 8.dp)
            .size(24.dp)
            .clip(ShapeDefaults.Large)
            .background(MaterialTheme.colorScheme.surface)
    )
}

@Composable
@Preview
fun ExerciseListPreview() {
    Workout__AndroidTheme {
        ExerciseList(
            exercisesList = List(10, { idx ->
                Exercise(
                    id = idx.toLong(),
                    trainingDayId = 0,
                    name = "Preview exercise",
                    reps = 10,
                    sets = 10,
                    rest = 60,
                    type = ExerciseType.DYNAMIC,
                    setsDone = 1,
                    order = idx
                )
            }),
            onReorder = { from, to -> },
            header = {},
            footer = {},
            onDeleteExercise = {},
            onSwipeToEditExercise = {},
            onDecrementExercise = {},
            onIncrementExercise = {},
            emptyMessage = {}
        )
    }
}
