package com.learning.workout__android.ui.screens.training

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.TrainingStatisticsItem

@Composable
fun TrainingStatistics(
    statistics:  List<TrainingStatisticsItem>,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clickable { expanded = true }
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text("Show statistics", fontSize = 14.sp)

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            statistics.forEach {
                StatisticItem(it)
            }
        }
    }
}

@Composable
private fun StatisticItem(item: TrainingStatisticsItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = item.exerciseName, fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(32.dp))
        Row() {
            if(item.exerciseType == ExerciseType.STATIC || item.exerciseType == ExerciseType.DYNAMIC) {
                Text(
                    text = "${item.repsDone}/${item.repsToDo} ${if (item.exerciseType == ExerciseType.STATIC) "sec" else "reps"}",
                    fontSize = 12.sp
                )
            }
            Box(modifier = Modifier.size(24.dp)) {
                if(item.repsDone >= item.repsToDo) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        tint = Color(0xFF14B8A6),
                        contentDescription = "Check",
                        modifier = Modifier.size(16.dp).align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun StatisticItemPreview() {
    Workout__AndroidTheme {
        Column {
            StatisticItem(
                item = TrainingStatisticsItem("Pull ups", ExerciseType.DYNAMIC, repsToDo = 20, repsDone = 10)
            )
            StatisticItem(
                item = TrainingStatisticsItem("Pull ups", ExerciseType.STATIC, repsToDo = 10, repsDone = 20)
            )
        }
    }
}