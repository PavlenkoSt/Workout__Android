package com.stanislav_pav.repstation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.stanislav_pav.repstation.data.AppDatabase
import com.stanislav_pav.repstation.data.models.Goal
import com.stanislav_pav.repstation.data.models.GoalsStatusEnum
import com.stanislav_pav.repstation.data.models.RecordModel
import com.stanislav_pav.repstation.data.repositories.GoalsRepository
import com.stanislav_pav.repstation.data.repositories.RecordsRepository
import com.stanislav_pav.repstation.utils.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val goalsRepository: GoalsRepository,
    private val recordsRepository: RecordsRepository
) : ViewModel() {
    val goals = goalsRepository.getAllGoals().distinctUntilChanged()
    private val records = recordsRepository.getAllRecords().distinctUntilChanged()

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

    fun saveGoalAsRecord(goal: Goal, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = recordsRepository.createRecordWithCheck(
                RecordModel(
                    name = goal.name,
                    count = goal.count,
                    units = goal.units
                )
            )

            onResult(success)
        }
    }

    val uiState = combine(
        goals,
        records,
        _goalToEdit,
        _filter
    ) { goals, records, goalsToEdit, filter ->
        val grouped = goals.groupingBy { it.status }.eachCount()

        GoalsUiState(
            goals = LoadState.Success(
                GroupedGoals(
                    completed = goals.filter { it.status == GoalsStatusEnum.Completed },
                    pending = goals.filter { it.status == GoalsStatusEnum.Pending }
                )
            ),
            goalToEdit = goalsToEdit,
            filter = filter,
            summary = GoalsStatusSummary(
                total = goals.size,
                completed = grouped[GoalsStatusEnum.Completed] ?: 0,
                pending = grouped[GoalsStatusEnum.Pending] ?: 0
            ),
            recordsCount = records.size,
            recordNames = records.map { it.name }.toSet()
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
                val recordsRepository = RecordsRepository(recordDao = db.recordDao())

                GoalsViewModel(goalsRepository, recordsRepository)
            }
        }
    }
}

data class GoalsUiState(
    val goals: LoadState<GroupedGoals> = LoadState.Loading,
    val goalToEdit: Goal? = null,
    val filter: GoalsFilterEnum = GoalsFilterEnum.All,
    val summary: GoalsStatusSummary = GoalsStatusSummary(),
    val recordsCount: Int = 0,
    val recordNames: Set<String> = emptySet()
)

data class GroupedGoals(
    val completed: List<Goal> = emptyList(),
    val pending: List<Goal> = emptyList()
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