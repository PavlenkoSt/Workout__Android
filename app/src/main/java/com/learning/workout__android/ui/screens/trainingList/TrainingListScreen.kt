package com.learning.workout__android.ui.screens.trainingList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.learning.workout__android.navigation.LocalNavController

@Composable
fun TrainingListScreen(
    modifier: Modifier
) {
    val navController = LocalNavController.current

    Column(modifier = modifier
        .fillMaxSize()
        .padding(top = 0.dp)) {
        TrainingListHeader(
            onBack = {
                navController.popBackStack()
            }
        )
    }
}