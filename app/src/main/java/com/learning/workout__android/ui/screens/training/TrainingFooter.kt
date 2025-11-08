package com.learning.workout__android.ui.screens.training

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

@Composable
fun TrainingFooter(
    text: String,
    onClick: () -> Unit
){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = onClick) {
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun TrainingFooterPreview() {
    Workout__AndroidTheme {
        TrainingFooter(text = "+ Add exercise", onClick = {})
    }
}