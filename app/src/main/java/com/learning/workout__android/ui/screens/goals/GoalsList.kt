package com.learning.workout__android.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.models.ExerciseUnits
import com.learning.workout__android.data.models.Goal
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.GoalsFilterEnum
import com.learning.workout__android.viewModel.GoalsStatusSummary
import com.learning.workout__android.viewModel.GroupedGoals

@Composable
fun GoalsList(
    goals: GroupedGoals,
    filter: GoalsFilterEnum,
    onIncrementGoal: (goal: Goal) -> Unit,
    onDecrementGoal: (goal: Goal) -> Unit,
    onEditGoalClick: (goal: Goal) -> Unit,
    onSaveGoalAsRecordClick: (goal: Goal) -> Unit,
    onDeleteGoalClick: (goal: Goal) -> Unit,
    summary: GoalsStatusSummary
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(bottom = 80.dp, end = 8.dp, start = 8.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if ((filter == GoalsFilterEnum.All || filter == GoalsFilterEnum.Pending) && goals.pending.isNotEmpty()) {
            if (filter == GoalsFilterEnum.All) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GoalListHeader("Pending (${summary.pending})")
                }
            }

            items(items = goals.pending, key = { it.id }) {
                GoalItem(
                    goal = it,
                    onDecrement = { onDecrementGoal(it) },
                    onIncrement = { onIncrementGoal(it) },
                    onDeleteClick = { onDeleteGoalClick(it) },
                    onSaveAsRecordClick = { onSaveGoalAsRecordClick(it) },
                    onEditClick = { onEditGoalClick(it) }
                )
            }
        }

        if ((filter == GoalsFilterEnum.All || filter == GoalsFilterEnum.Completed) && goals.completed.isNotEmpty()) {
            if (filter == GoalsFilterEnum.All) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    GoalListHeader("Completed (${summary.completed})")
                }
            }

            items(items = goals.completed, key = { it.id }) {
                GoalItem(
                    goal = it,
                    onDecrement = { onDecrementGoal(it) },
                    onIncrement = { onIncrementGoal(it) },
                    onDeleteClick = { onDeleteGoalClick(it) },
                    onSaveAsRecordClick = { onSaveGoalAsRecordClick(it) },
                    onEditClick = { onEditGoalClick(it) }
                )
            }
        }
    }
}

@Composable
private fun GoalListHeader(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .clip(ShapeDefaults.Medium)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .border(1.dp, MaterialTheme.colorScheme.onSecondaryContainer, ShapeDefaults.Medium)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    )
}

@Composable
@Preview(showBackground = true)
private fun GoalsListPreview() {
    Workout__AndroidTheme {
        GoalsList(
            goals = GroupedGoals(
                pending = List(5, {
                    Goal(
                        id = it.toLong(),
                        name = "Pull ups",
                        count = 12,
                        targetCount = 20,
                        units = ExerciseUnits.REPS
                    )
                }),
            ),
            filter = GoalsFilterEnum.All,
            onIncrementGoal = {},
            onDecrementGoal = {},
            onEditGoalClick = {},
            onDeleteGoalClick = {},
            onSaveGoalAsRecordClick = {},
            summary = GoalsStatusSummary()
        )
    }
}