package com.learning.workout__android.ui.screens.presets

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DeletePresetConfirmDialog(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    presetName: String
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        },
        title = {
            Text(text = "Do you really want to delete preset \"$presetName\" ?")
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