package com.learning.workout__android.ui.screens.trainingList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TrainingListScreen(
    modifier: Modifier
) {
    Column (modifier = modifier.fillMaxSize()) {
        TrainingListHeader()
    }
}