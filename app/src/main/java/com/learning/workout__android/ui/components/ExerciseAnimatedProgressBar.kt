package com.learning.workout__android.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ExerciseAnimatedProgressBar(
    targetCount: Int,
    count: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (count.toFloat() / targetCount.toFloat()),
        label = "progress"
    )

    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${count}/${targetCount}", fontSize = 14.sp)
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth(),
            trackColor = MaterialTheme.colorScheme.surface,
            color = MaterialTheme.colorScheme.primary,
            strokeCap = StrokeCap.Round,
        )
    }
}