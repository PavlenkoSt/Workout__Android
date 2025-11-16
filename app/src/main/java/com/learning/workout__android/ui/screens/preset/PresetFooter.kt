package com.learning.workout__android.ui.screens.preset

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

@Composable
fun PresetFooter(onAddExerciseClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onAddExerciseClick,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("+ Add")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PresetFooterPreview() {
    Workout__AndroidTheme {
        PresetFooter(onAddExerciseClick = {})
    }
}