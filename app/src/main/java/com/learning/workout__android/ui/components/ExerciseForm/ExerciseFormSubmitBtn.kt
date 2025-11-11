package com.learning.workout__android.ui.components.ExerciseForm

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ExerciseFormSubmitBtn(
    onClick: () -> Unit,
    isEditing: Boolean
) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = if (isEditing) {
                "Update"
            } else {
                "Add +"
            }
        )
    }
}