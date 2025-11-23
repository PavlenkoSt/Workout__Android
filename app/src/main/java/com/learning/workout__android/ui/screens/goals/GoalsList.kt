package com.learning.workout__android.ui.screens.goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.models.Goal
import com.learning.workout__android.data.models.GoalUnits
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

@Composable
fun GoalsList(
    goals: List<Goal>,
    onIncrementGoal: (goal: Goal) -> Unit,
    onDecrementGoal: (goal: Goal) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            end = 8.dp, start = 8.dp, bottom = 80.dp, top = 12.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = goals, key = { it.id }) {
            GoalItem(
                goal = it,
                onDecrement = { onDecrementGoal(it) },
                onIncrement = { onIncrementGoal(it) }
            )
        }
    }
}

@Composable
@Preview
private fun GoalsListPreview() {
    Workout__AndroidTheme {
        GoalsList(
            goals = List(5, {
                Goal(
                    id = it.toLong(),
                    name = "Pull ups",
                    count = 12,
                    targetCount = 20,
                    units = GoalUnits.REPS
                )
            }),
            onIncrementGoal = {},
            onDecrementGoal = {}
        )
    }
}