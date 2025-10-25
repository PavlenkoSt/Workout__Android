package com.learning.workout__android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.TrainingDay
import com.learning.workout__android.data.repositories.TrainingDayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrainingViewModel (
  private val trainingDayRepository: TrainingDayRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TrainingDayUiState())
    val uiState: StateFlow<TrainingDayUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            trainingDayRepository.getAll().collectLatest {
                _uiState.update { state ->
                   state.copy(
                       trainingDays = it
                   )
                }
            }
        }
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getDatabase(context)
                val airportRepository = TrainingDayRepository(trainingDayDao = db.trainingDayDao())

                TrainingViewModel(
                    trainingDayRepository = airportRepository
                )
            }
        }
    }
}

data class TrainingDayUiState(
    val trainingDays: List<TrainingDay> = emptyList()
)