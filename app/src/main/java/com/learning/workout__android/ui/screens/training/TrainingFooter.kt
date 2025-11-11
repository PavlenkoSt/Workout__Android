package com.learning.workout__android.ui.screens.training

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.TrainingStatisticsItem

@Composable
fun TrainingFooter(
    text: String,
    onClick: () -> Unit,
    statistics: List<TrainingStatisticsItem>
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (statistics.isNotEmpty()) {
            TrainingStatistics(
                statistics = statistics,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        Button(onClick = onClick, modifier = Modifier.align(Alignment.Center)) {
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingFooterPreview() {
    Workout__AndroidTheme {
        TrainingFooter(text = "+ Add exercise", onClick = {}, statistics = emptyList())
    }
}