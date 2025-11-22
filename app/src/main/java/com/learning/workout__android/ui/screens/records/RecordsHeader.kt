package com.learning.workout__android.ui.screens.records

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.SortField
import com.learning.workout__android.viewModel.SortOrder
import com.learning.workout__android.viewModel.SortState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsHeader(
    sortState: SortState,
    onSortChange: (field: SortField) -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset((-2).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SortableHeaderItem(
                    text = "Exercise",
                    field = SortField.EXERCISE,
                    sortState = sortState,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start,
                    onSort = { onSortChange(it) }
                )

                SortableHeaderItem(
                    text = "Result",
                    field = SortField.RESULT,
                    sortState = sortState,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    onSort = { onSortChange(it) }
                )

                SortableHeaderItem(
                    text = "Date",
                    field = SortField.DATE,
                    sortState = sortState,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    onSort = { onSortChange(it) }
                )
            }
        },
        actions = {
            Spacer(modifier = Modifier.width(12.dp))
        }
    )
}

@Composable
private fun SortableHeaderItem(
    text: String,
    field: SortField,
    sortState: SortState,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    onSort: (SortField) -> Unit
) {
    val isActive = sortState.field == field
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onBackground

    Row(
        modifier = modifier
            .clickable { onSort(field) },
        horizontalArrangement = when (textAlign) {
            TextAlign.End -> androidx.compose.foundation.layout.Arrangement.End
            TextAlign.Center -> androidx.compose.foundation.layout.Arrangement.Center
            else -> androidx.compose.foundation.layout.Arrangement.Start
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            color = if (isActive) activeColor else inactiveColor,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
            textAlign = textAlign
        )

        if (isActive) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = when (sortState.order) {
                    SortOrder.ASC -> Icons.Filled.ArrowDropUp
                    SortOrder.DESC -> Icons.Filled.ArrowDropDown
                },
                contentDescription = "Sort direction",
                tint = activeColor,
                modifier = Modifier
                    .width(20.dp)
                    .offset((-4).dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun RecordsHeaderPreview() {
    Workout__AndroidTheme {
        RecordsHeader(
            onSortChange = {},
            sortState = SortState()
        )
    }
}