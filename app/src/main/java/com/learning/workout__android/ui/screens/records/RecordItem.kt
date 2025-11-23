package com.learning.workout__android.ui.screens.records

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.data.models.ExerciseUnits
import com.learning.workout__android.data.models.RecordModel
import com.learning.workout__android.ui.components.SwipeToDismissItemBackground
import com.learning.workout__android.ui.screens.presets.DeleteRecordConfirmDialog
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatTimestamp
import java.util.Locale.getDefault

@Composable
fun RecordItem(
    record: RecordModel,
    onSwipeToEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var openAlertDialog by remember { mutableStateOf(false) }

    val swipeToDismissBoxState = key(record) {
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

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = Modifier.fillMaxWidth(),
        backgroundContent = {
            SwipeToDismissItemBackground(swipeToDismissBoxState.dismissDirection)
        }
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = record.name,
                    modifier = Modifier
                        .weight(1f),
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${record.count} ${
                        record.units.toString()
                            .lowercase()
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(getDefault()) else it.toString() }
                    }",
                    modifier = Modifier
                        .weight(1f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatTimestamp(record.createdAt),
                    modifier = Modifier
                        .weight(1f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.End
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }

    if(openAlertDialog) {
        DeleteRecordConfirmDialog(
            onDelete = onDelete,
            onCancel = { openAlertDialog = false },
            recordName = record.name
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun RecordItemPreview() {
    Workout__AndroidTheme {
        RecordItem(
            RecordModel(name = "Pull ups", count = 20, units = ExerciseUnits.REPS),
            onSwipeToEdit = {},
            onDelete = {}
        )
    }
}