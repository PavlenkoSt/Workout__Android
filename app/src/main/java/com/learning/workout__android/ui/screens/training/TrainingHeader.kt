package com.learning.workout__android.ui.screens.training

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatDate
import java.time.LocalDate

@Composable
fun TrainingHeader(
    currentDate: LocalDate,
    modifier: Modifier = Modifier,
    isTrainingDay: Boolean,
    hasExercises: Boolean,
    onDeleteTrainingDay: () -> Unit,
    onSaveAsPresetClick: () -> Unit
) {
    var dropdownMenuExpanded by remember { mutableStateOf(false) }
    var openAlertDialog by remember { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clickable(enabled = isTrainingDay) {
                dropdownMenuExpanded = true
            }
            .padding(all = 8.dp)
    ) {
        Text(
            text = "Workout session - ${formatDate(currentDate)}",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        DropdownMenu(
            expanded = dropdownMenuExpanded,
            onDismissRequest = { dropdownMenuExpanded = false }
        ) {
            if (hasExercises) {
                DropdownMenuItem(
                    text = { Text("Save as preset") },
                    onClick = {
                        onSaveAsPresetClick()
                        dropdownMenuExpanded = false
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Save")
                    }
                )
            }
            DropdownMenuItem(
                text = { Text("Delete training day", color = Color.Red) },
                onClick = {
                    openAlertDialog = true
                    dropdownMenuExpanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            )
        }
    }

    if (openAlertDialog) {
        DeleteTrainingDayConfirmDialog(
            onDelete = {
                onDeleteTrainingDay()
                openAlertDialog = false
            },
            onCancel = {
                openAlertDialog = false
            })
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingHeaderPreview() {
    Workout__AndroidTheme {
        TrainingHeader(
            currentDate = LocalDate.now(),
            onDeleteTrainingDay = {},
            onSaveAsPresetClick = {},
            isTrainingDay = false,
            hasExercises = true
        )
    }
}