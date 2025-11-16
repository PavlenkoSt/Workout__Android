package com.learning.workout__android.ui.screens.presets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.models.Preset
import com.learning.workout__android.data.models.PresetWithExercises
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

@Composable
fun PresetItem(
    preset: PresetWithExercises,
    isDragging: Boolean,
    draggableHandler: @Composable () -> Unit,
    onSwipeToEdit: () -> Unit,
    onSwipeToDelete: () -> Unit,
) {
    var openAlertDialog by remember { mutableStateOf(false) }

    val swipeToDismissBoxState = key(preset.preset.id) {
        rememberSwipeToDismissBoxState(
            confirmValueChange = {
                if (it == SwipeToDismissBoxValue.EndToStart) {
                    openAlertDialog = true
                } else if (it == SwipeToDismissBoxValue.StartToEnd) {
                    onSwipeToEdit()
                }
                false
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
                BackgroundContent(swipeToDismissBoxState.dismissDirection)
            }
        ) {
            Card(
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
                        "${preset.preset.name} (${preset.sortedExercises.size} exercise${if (preset.sortedExercises.size == 1) "" else "s"})"
                    )

                    draggableHandler()
                }
            }
        }
    }

   if(openAlertDialog) {
        DeletePresetConfirmDialog(
            onDelete = onSwipeToDelete,
            onCancel = { openAlertDialog = false },
            presetName = preset.preset.name
        )
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
@Preview(showBackground = true)
private fun PresetItemPreview() {
    Workout__AndroidTheme {
        PresetItem(
            preset = PresetWithExercises(
                preset = Preset(id = 0L, name = "Handstand training", order = 1L),
                exercises = emptyList()
            ),
            isDragging = false,
            draggableHandler = {},
            onSwipeToEdit = {},
            onSwipeToDelete = {}
        )
    }
}