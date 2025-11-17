package com.learning.workout__android.data.repositories

import com.learning.workout__android.data.daos.GoalDao
import com.learning.workout__android.data.models.Goal
import kotlinx.coroutines.flow.Flow

class GoalsRepository(
    private val goalsDao: GoalDao
) {
    fun getAllGoals(): Flow<List<Goal>> {
        return goalsDao.getAll()
    }

    fun createGoal(goal: Goal) {
        goalsDao.create(goal)
    }

    fun updateGoal(goal: Goal) {
        goalsDao.update(goal)
    }

    fun deleteGoal(goal: Goal) {
        goalsDao.delete(goal)
    }
}