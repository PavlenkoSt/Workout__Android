package com.learning.workout__android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.Goal
import com.learning.workout__android.data.models.GoalsStatusEnum
import com.learning.workout__android.data.repositories.GoalsRepository
import com.learning.workout__android.utils.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val goalsRepository: GoalsRepository
) : ViewModel() {
    val goals = goalsRepository.getAllGoals().distinctUntilChanged()

    private var _goalToEdit = MutableStateFlow<Goal?>(null)

    private var _filter = MutableStateFlow(GoalsFilterEnum.All)

    fun setFilter(filter: GoalsFilterEnum) {
        _filter.value = filter
    }

    fun setGoalToEdit(goal: Goal?) {
        _goalToEdit.value = goal
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            goalsRepository.deleteGoal(goal)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            goalsRepository.updateGoal(goal)
        }
    }

    fun createGoal(goal: Goal) {
        viewModelScope.launch {
            goalsRepository.createGoal(goal)
        }
    }

    val uiState = combine(
        goals,
        _goalToEdit,
        _filter
    ) { goals, goalsToEdit, filter ->
        val filteredGoals = when (filter) {
            GoalsFilterEnum.Pending -> goals.filter { it.status == GoalsStatusEnum.Pending }
            GoalsFilterEnum.Completed -> goals.filter { it.status == GoalsStatusEnum.Completed }
            GoalsFilterEnum.All -> goals
        }

        val grouped = goals.groupingBy { it.status }.eachCount()

        GoalsUiState(
            goals = LoadState.Success(filteredGoals),
            goalToEdit = goalsToEdit,
            filter = filter,
            summary = GoalsStatusSummary(
                total = goals.size,
                completed = grouped[GoalsStatusEnum.Completed] ?: 0,
                pending = grouped[GoalsStatusEnum.Pending] ?: 0
            )
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GoalsUiState()
    )

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getDatabase(context)
                val goalsRepository = GoalsRepository(goalsDao = db.goalDao())

                GoalsViewModel(goalsRepository)
            }
        }
    }
}

data class GoalsUiState(
    val goals: LoadState<List<Goal>> = LoadState.Loading,
    val goalToEdit: Goal? = null,
    val filter: GoalsFilterEnum = GoalsFilterEnum.All,
    val summary: GoalsStatusSummary = GoalsStatusSummary()
)

enum class GoalsFilterEnum {
    All,
    Completed,
    Pending
}

data class GoalsStatusSummary(
    val total: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0
)