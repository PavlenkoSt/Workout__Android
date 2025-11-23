package com.learning.workout__android.ui.screens.goals

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.GoalsFilterEnum
import com.learning.workout__android.viewModel.GoalsStatusSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsFilter(
    filter: GoalsFilterEnum,
    onFilterChange: (GoalsFilterEnum) -> Unit,
    summary: GoalsStatusSummary
) {
    val filterValues = listOf(
        GoalsFilterEnum.All,
        GoalsFilterEnum.Completed,
        GoalsFilterEnum.Pending
    )

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        Row(
            modifier = Modifier
                .border(
                    2.dp,
                    color = MaterialTheme.colorScheme.onBackground,
                    shape = ShapeDefaults.Small
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .menuAnchor()
                .defaultMinSize(100.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "$filter (${getSummaryForFilter(filter, summary)})",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded, modifier = Modifier)
        }


        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            filterValues.forEach { item ->
                DropdownMenuItem(
                    text = { Text("$item (${getSummaryForFilter(item, summary)})") },
                    onClick = {
                        onFilterChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun getSummaryForFilter(
    filter: GoalsFilterEnum,
    summary: GoalsStatusSummary
): Int {
    return when (filter) {
        GoalsFilterEnum.All -> summary.total
        GoalsFilterEnum.Completed -> summary.completed
        GoalsFilterEnum.Pending -> summary.pending
    }
}

@Composable
@Preview()
private fun GoalsFilterPreview() {
    Workout__AndroidTheme {
        GoalsFilter(
            filter = GoalsFilterEnum.All,
            onFilterChange = {},
            summary = GoalsStatusSummary()
        )
    }
}