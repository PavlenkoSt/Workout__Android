package com.learning.workout__android.ui.screens.preset

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.PresetExercise
import com.learning.workout__android.ui.components.DraggableHandler
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PresetExerciseList(
    modifier: Modifier = Modifier,
    exercises: List<PresetExercise>,
    footer: @Composable () -> Unit,
    onSwipeToEdit: (exercise: PresetExercise) -> Unit,
    onSwipeToDelete: (exercise: PresetExercise) -> Unit,
    reorderExercises: (from: PresetExercise, to: PresetExercise) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val adjustedFrom = from.index
        val adjustedTo = to.index

        if (adjustedFrom in exercises.indices && adjustedTo in 0..exercises.size) {
            reorderExercises(exercises[adjustedFrom], exercises[adjustedTo])
        }
    }

    val canReorder = exercises.size > 1

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(end = 8.dp, start = 8.dp, bottom = 12.dp, top = 12.dp)
    ) {
        itemsIndexed(exercises, key = { _, exercise -> exercise.id }) { index, exercise ->
            ReorderableItem(
                reorderableLazyListState,
                key = exercise.id,
                enabled = canReorder,
            ) { isDragging ->
                PresetExerciseItem(
                    exercise = exercise,
                    index = index,
                    onSwipeToDelete = { onSwipeToDelete(exercise) },
                    onSwipeToEdit = { onSwipeToEdit(exercise) },
                    isDragging = isDragging,
                    draggableHandler = {
                        if (canReorder) {
                            DraggableHandler(
                                modifier = Modifier
                                    .draggableHandle()
                                    .longPressDraggableHandle()
                            )
                        }
                    }
                )
            }
        }

        item(key = "footer") {
            footer()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PresetExerciseListPreview() {
    Workout__AndroidTheme {
        PresetExerciseList(
            exercises = List(
                5, { index ->
                    PresetExercise(
                        presetId = 1L,
                        name = "Pull ups",
                        rest = 60,
                        sets = 4,
                        reps = 12,
                        order = index,
                        type = ExerciseType.DYNAMIC,
                        id = index.toLong()
                    )
                }
            ),
            footer = {},
            onSwipeToDelete = {},
            onSwipeToEdit = {},
            reorderExercises = {
                from, to ->
            },
        )
    }
}