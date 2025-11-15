package com.learning.workout__android.ui.screens.trainingList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.learning.workout__android.data.models.TrainingDay
import com.learning.workout__android.data.models.TrainingDayStatus
import com.learning.workout__android.data.models.TrainingDayWithExercises
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import java.time.LocalDate

@Composable
fun TrainingItem(trainingDay: TrainingDayWithExercises, onGoToTrainingDay: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "accordion-arrow"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        border = CardDefaults.outlinedCardBorder(enabled = true)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(trainingDay.trainingDay.date, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = trainingDay.status.toString(),
                fontSize = 12.sp,
                color = getStatusColor(trainingDay.status)
            )
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                // arrow rotation based on the expanded state
                modifier = Modifier.rotate(arrowRotation)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            // animate expand vertically from the top when expanded + fade in
            enter = expandVertically(
                expandFrom = Alignment.Top,
                animationSpec = tween()
            ) + fadeIn(),
            // animate shrink vertically to the top when collapsed + fade out
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween()
            ) + fadeOut()
        ) {
            TrainingItemDetails(trainingDay = trainingDay, onGoToTrainingDay)
        }
    }
}

private fun getStatusColor(status: TrainingDayStatus): Color {
    return when (status) {
        TrainingDayStatus.Completed -> Color(0xFF37A63A)
        TrainingDayStatus.Failed -> Color(0xFFF44336)
        TrainingDayStatus.Pending -> Color(0xFFFF8F07)
    }
}

@Composable
@Preview(showBackground = true)
private fun TrainingItemPreview() {
    Workout__AndroidTheme {
        TrainingItem(
            TrainingDayWithExercises(
                trainingDay = TrainingDay(id = 0L, date = LocalDate.now().toString()),
                exercises = emptyList()
            ),
            onGoToTrainingDay = {}
        )
    }
}