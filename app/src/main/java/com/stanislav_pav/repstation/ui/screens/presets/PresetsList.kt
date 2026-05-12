package com.stanislav_pav.repstation.ui.screens.presets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stanislav_pav.repstation.data.models.Preset
import com.stanislav_pav.repstation.data.models.PresetWithExercises
import com.stanislav_pav.repstation.ui.components.DraggableHandler
import com.stanislav_pav.repstation.ui.theme.RepStationTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PresetsList(
    presets: List<PresetWithExercises>,
    reorderPresets: (
        from: PresetWithExercises,
        to: PresetWithExercises
    ) -> Unit,
    onSwipeToEdit: (PresetWithExercises) -> Unit,
    onSwipeToDelete: (PresetWithExercises) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val adjustedFrom = from.index
        val adjustedTo = to.index

        if (adjustedFrom in presets.indices && adjustedTo in presets.indices) {
            reorderPresets(presets[adjustedFrom], presets[adjustedTo])
        }
    }

    val canReorder = presets.size > 1

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(
            end = 8.dp, start = 8.dp, bottom = 80.dp, top = 12.dp
        ),
    ) {
        items(items = presets, key = { it.preset.id }) {
            ReorderableItem(
                reorderableLazyListState,
                key = it.preset.id,
                enabled = canReorder,
            ) { isDragging ->
                PresetItem(
                    preset = it,
                    isDragging = isDragging,
                    onSwipeToEdit = { onSwipeToEdit(it) },
                    onSwipeToDelete = { onSwipeToDelete(it) },
                    draggableHandler = {
                        if (canReorder) {
                            DraggableHandler(
                                modifier = Modifier
                                    .draggableHandle()
                                    .longPressDraggableHandle()
                            )
                        }
                    })
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PresetsListPreview() {
    RepStationTheme {
        PresetsList(
            presets = List(5, {
                PresetWithExercises(
                    preset = Preset(id = it.toLong(), name = "Handbalance session", order = 1L),
                    exercises = emptyList()
                )
            }),
            reorderPresets = { from, to -> },
            onSwipeToEdit = {},
            onSwipeToDelete = {}
        )
    }
}
