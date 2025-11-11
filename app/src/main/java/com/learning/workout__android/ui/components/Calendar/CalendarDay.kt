package com.learning.workout__android.ui.components.Calendar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.models.TrainingDayStatus
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.CalendarUiModel
import java.time.LocalDate


@Composable
fun CalendarDay(
    date: CalendarUiModel.Date,
    onClick: (CalendarUiModel.Date) -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean,
    trainingDayStatus: TrainingDayStatus
) {
    Card(
        modifier = modifier.clickable { onClick(date) },
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.scrim
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainer
            }
        ),
    ) {
        Box() {
            Column(
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = date.day,
                    style = MaterialTheme.typography.bodySmall,
                    color = if(isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = date.date.dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if(isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
            }

            when(trainingDayStatus) {
                TrainingDayStatus.COMPLETED -> {
                    DayStatusMark(
                        imageVector = Icons.Default.Check,
                        color = Color(0xFF14B8A6),
                        isToday = date.isToday
                    )
                }
                TrainingDayStatus.FAILED -> {
                    DayStatusMark(
                        imageVector = Icons.Default.Close,
                        color = Color.Red,
                        isToday = date.isToday
                    )
                }
                TrainingDayStatus.PENDING -> {
                    DayStatusMark(
                        imageVector = Icons.Default.PlayArrow,
                        color = Color(0xFFffb800),
                        isToday = date.isToday
                    )
                }
                else -> {}
            }
        }

        if (date.isToday) {
            Text(
                text = "Today",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
fun BoxScope.DayStatusMark (
    imageVector: ImageVector,
    color: Color,
    isToday: Boolean
) {
    Box(
        modifier = Modifier
            .offset(x = (-4).dp, y = if(isToday) -(1).dp else (-4).dp)
            .background(MaterialTheme.colorScheme.onPrimary, shape = ShapeDefaults.Large)
            .padding(1.dp)
            .background(color, shape = ShapeDefaults.Large)
            .align(Alignment.BottomEnd)
        ,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Status",
            modifier = Modifier.size(14.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarTodayDayPreview() {
    Workout__AndroidTheme {
        CalendarDay(
            date = CalendarUiModel.Date(
                date = LocalDate.now(),
                isSelected = false,
                isToday = true
            ),
            onClick = {},
            isActive = true,
            modifier = Modifier.width(64.dp),
            trainingDayStatus = TrainingDayStatus.COMPLETED
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarPendingDayPreview() {
    Workout__AndroidTheme {
        CalendarDay(
            date = CalendarUiModel.Date(
                date = LocalDate.now(),
                isSelected = false,
                isToday = false
            ),
            onClick = {},
            isActive = false,
            modifier = Modifier.width(64.dp),
            trainingDayStatus = TrainingDayStatus.PENDING
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarCompletedDayPreview() {
    Workout__AndroidTheme {
        CalendarDay(
            date = CalendarUiModel.Date(
                date = LocalDate.now(),
                isSelected = false,
                isToday = false
            ),
            onClick = {},
            isActive = false,
            modifier = Modifier.width(64.dp),
            trainingDayStatus = TrainingDayStatus.COMPLETED
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CalendarFailedDayPreview() {
    Workout__AndroidTheme {
        CalendarDay(
            date = CalendarUiModel.Date(
                date = LocalDate.now(),
                isSelected = false,
                isToday = false
            ),
            onClick = {},
            isActive = false,
            modifier = Modifier.width(64.dp),
            trainingDayStatus = TrainingDayStatus.FAILED
        )
    }
}