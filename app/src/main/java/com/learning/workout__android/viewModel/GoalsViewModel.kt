package com.learning.workout__android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.Goal
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

    fun setGoalToEdit(goal: Goal?) {
        _goalToEdit.value = goal
    }

    fun deleteRecord(goal: Goal) {
        viewModelScope.launch {
            goalsRepository.deleteGoal(goal)
        }
    }

    fun updateRecord(goal: Goal) {
        viewModelScope.launch {
            goalsRepository.updateGoal(goal)
        }
    }

    fun createRecord(goal: Goal) {
        viewModelScope.launch {
            goalsRepository.createGoal(goal)
        }
    }

    val uiState = combine(
        goals,
        _goalToEdit
    ) { goals, goalsToEdit ->
        GoalsUiState(
            goals = LoadState.Success(goals),
            goalToEdit = goalsToEdit
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
    val goalToEdit: Goal? = null
)