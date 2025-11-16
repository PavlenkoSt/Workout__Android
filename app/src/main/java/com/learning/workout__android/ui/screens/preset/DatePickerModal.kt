package com.learning.workout__android.ui.screens.preset

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    trainingDayDates: List<String>,
) {
    val blockedDates: Set<LocalDate> = trainingDayDates
        .map { LocalDate.parse(it) }
        .toSet()

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                // Disable if this date is in blocked list
                return date !in blockedDates
            }

            override fun isSelectableYear(year: Int): Boolean = true
        }
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
            }) {

                Text("Use")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}