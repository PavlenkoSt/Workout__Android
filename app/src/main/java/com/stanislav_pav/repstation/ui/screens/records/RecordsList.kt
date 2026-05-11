package com.stanislav_pav.repstation.ui.screens.records

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stanislav_pav.repstation.data.models.ExerciseUnits
import com.stanislav_pav.repstation.data.models.RecordModel
import com.stanislav_pav.repstation.ui.theme.RepStationTheme

@Composable
fun RecordsList(
    records: List<RecordModel>,
    onDeleteRecord: (record: RecordModel) -> Unit,
    onSwipeToEdit: (record: RecordModel) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(
            end = 8.dp, start = 8.dp, bottom = 80.dp, top = 12.dp
        )
    ) {
        items(items = records, key = { it.id }) {
            RecordItem(
                record = it,
                onDelete = { onDeleteRecord(it) },
                onSwipeToEdit = { onSwipeToEdit(it) }
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
    }


}

@Composable
@Preview(showBackground = true)
private fun RecordsListPreview() {
    RepStationTheme {
        RecordsList(
            List(5, {
                RecordModel(
                    id = it.toLong(),
                    name = "Pull ups",
                    count = 20,
                    units = ExerciseUnits.REPS
                )
            }),
            onSwipeToEdit = {},
            onDeleteRecord = {}
        )
    }
}