package com.learning.workout__android.ui.screens.goals

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeleteGoalConfirmDialog(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    goalName: String
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        },
        title = {
            Text(text = "Do you really want to delete goal \"$goalName\" ?")
        },
        onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Dismiss")
            }
        }
    )
}