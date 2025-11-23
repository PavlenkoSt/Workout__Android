package com.learning.workout__android.ui.screens.goals

import DrawingCheckmark
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.models.ExerciseUnits
import com.learning.workout__android.data.models.Goal
import com.learning.workout__android.data.models.GoalsStatusEnum
import com.learning.workout__android.ui.components.ExerciseAnimatedProgressBar
import com.learning.workout__android.ui.components.ExerciseCountUpdateBtn
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatExerciseType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalItem(
    goal: Goal,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSaveAsRecordClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var openAlertDialog by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 4.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                Text(
                    text = "${goal.name} (${formatExerciseType(goal.units.name)})",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = {
                            expanded = false
                            onEditClick()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Delete", color = Color.Red) },
                        onClick = {
                            expanded = false
                            openAlertDialog = true
                        }
                    )

                    if (goal.status == GoalsStatusEnum.Completed) {
                        DropdownMenuItem(
                            text = { Text("Move to records") },
                            onClick = {
                                expanded = false
                                onSaveAsRecordClick()
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseCountUpdateBtn(onClick = onDecrement, text = "-")

                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    val isCheckMarkVisible = goal.count >= goal.targetCount

                    ExerciseAnimatedProgressBar(
                        count = goal.count,
                        targetCount = goal.targetCount
                    )

                    if (isCheckMarkVisible) {
                        DrawingCheckmark(
                            isVisible = true,
                            size = 28,
                            modifier = Modifier
                                .align(alignment = Alignment.BottomEnd)
                                .offset(x = 8.dp, y = 4.dp),
                            backgroundColor = Color(0xFF14B8A6)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                ExerciseCountUpdateBtn(onClick = onIncrement, text = "+")
            }
        }
    }

    if(openAlertDialog) {
        DeleteGoalConfirmDialog(
            onDelete = onDeleteClick,
            onCancel = { openAlertDialog = false },
            goalName = goal.name
        )
    }
}

@Composable
@Preview
private fun GoalItemPreview() {
    Workout__AndroidTheme {
        GoalItem(
            goal = Goal(
                id = 0L,
                name = "Pull ups",
                count = 12,
                targetCount = 20,
                units = ExerciseUnits.REPS
            ),
            onDecrement = {},
            onIncrement = {},
            onEditClick = {},
            onDeleteClick = {},
            onSaveAsRecordClick = {}
        )
    }
}