package com.learning.workout__android.ui.screens.trainingList

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.learning.workout__android.navigation.LocalNavController
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

@Composable
fun TrainingListHeader() {
    val navController = LocalNavController.current

    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = { navController.popBackStack() }
        ) {
            Icon(imageVector = Icons.Default.ArrowBackIosNew, contentDescription = null)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TrainingListHeaderPreview() {
    Workout__AndroidTheme {
        TrainingListHeader()
    }
}