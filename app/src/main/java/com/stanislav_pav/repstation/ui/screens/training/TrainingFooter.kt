package com.stanislav_pav.repstation.ui.screens.training

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.stanislav_pav.repstation.ui.theme.RepStationTheme
import com.stanislav_pav.repstation.viewModel.TrainingStatisticsItem

@Composable
fun TrainingFooter(
    text: String,
    onClick: () -> Unit,
    statistics: List<TrainingStatisticsItem>,
    statisticsLocked: Boolean = false,
    onStatisticsLockedClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (statistics.isNotEmpty()) {
            if (statisticsLocked) {
                Button(
                    onClick = onStatisticsLockedClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text("Pro statistics")
                }
            } else {
                TrainingStatistics(
                    statistics = statistics,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        }
        Button(onClick = onClick, modifier = Modifier.align(Alignment.Center)) {
            Text(text = text)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrainingFooterPreview() {
    RepStationTheme {
        TrainingFooter(text = "+ Add exercise", onClick = {}, statistics = emptyList())
    }
}
