package com.learning.workout__android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.TrainingDayStatus
import com.learning.workout__android.data.models.TrainingDayWithExercises
import com.learning.workout__android.data.repositories.TrainingDayRepository
import com.learning.workout__android.utils.LoadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TrainingListViewModel(
    private val trainingDayRepository: TrainingDayRepository
) : ViewModel() {
    private val _filter = MutableStateFlow(TrainingListFilterEnum.All)
    val filter: StateFlow<TrainingListFilterEnum> = _filter

    fun setFilter(newFilter: TrainingListFilterEnum) {
        _filter.value = newFilter
    }

    val trainingDays: Flow<LoadState<List<TrainingDayWithExercises>>> =
        trainingDayRepository.getAllTrainingDays()
            .distinctUntilChanged()
            .map { LoadState.Success(it) }

    val uiState: StateFlow<TrainingListUiState> =
        combine(
            trainingDays,
            filter
        ) { daysState, filter ->
            var summary = TrainingStatusSummary()

            val filtered = when (daysState) {
                is LoadState.Success -> {
                    val allDays = daysState.data

                    // --- summary for ALL days ---
                    val grouped = allDays.groupingBy { it.status }.eachCount()

                    summary = TrainingStatusSummary(
                        total = allDays.size,
                        completed = grouped[TrainingDayStatus.Completed] ?: 0,
                        pending = grouped[TrainingDayStatus.Pending] ?: 0,
                        failed = grouped[TrainingDayStatus.Failed] ?: 0
                    )

                    allDays.filter { day ->
                        when (filter) {
                            TrainingListFilterEnum.All -> true
                            TrainingListFilterEnum.Completed ->
                                day.status == TrainingDayStatus.Completed

                            TrainingListFilterEnum.Pending ->
                                day.status == TrainingDayStatus.Pending

                            TrainingListFilterEnum.Failed ->
                                day.status == TrainingDayStatus.Failed
                        }
                    }
                }

                else -> emptyList()
            }

            TrainingListUiState(
                trainingDays = LoadState.Success(filtered),
                filter = filter,
                summary = summary
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                TrainingListUiState()
            )

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getDatabase(context)
                val airportRepository = TrainingDayRepository(trainingDayDao = db.trainingDayDao())

                TrainingListViewModel(
                    trainingDayRepository = airportRepository
                )
            }
        }
    }
}

data class TrainingListUiState(
    val trainingDays: LoadState<List<TrainingDayWithExercises>> = LoadState.Loading,
    val filter: TrainingListFilterEnum = TrainingListFilterEnum.All,
    val summary: TrainingStatusSummary = TrainingStatusSummary()
)

enum class TrainingListFilterEnum {
    All,
    Completed,
    Pending,
    Failed
}

data class TrainingStatusSummary(
    val total: Int = 0,
    val completed: Int = 0,
    val pending: Int = 0,
    val failed: Int = 0
)