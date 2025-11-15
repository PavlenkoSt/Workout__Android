package com.learning.workout__android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.PresetWithExercises
import com.learning.workout__android.data.repositories.PresetsRepository
import com.learning.workout__android.utils.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

class PresetsViewModel(
    private val presetsRepository: PresetsRepository
) : ViewModel() {
    val allPresets = presetsRepository.getAllPresets().distinctUntilChanged()

    private var _selectedPresetId = MutableStateFlow<Long?>(null)
    val selectedPresetId: StateFlow<Long?> = _selectedPresetId

    fun selectPresetId(presetId: Long?) {
        _selectedPresetId.value = presetId
    }

    val uiState: StateFlow<PresetsUiState> =
        combine(
            allPresets,
            selectedPresetId
        ) { allPresets, selectedPresetId ->
            PresetsUiState(
                allPresets = LoadState.Success(
                    allPresets,
                ),
                selectedPreset = if (selectedPresetId != null) {
                    allPresets.find { it.preset.id == selectedPresetId }
                } else null
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                PresetsUiState()
            )

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getDatabase(context)
                val presetsRepository = PresetsRepository(presetDao = db.presetDao())

                PresetsViewModel(presetsRepository)
            }
        }
    }

}

data class PresetsUiState(
    val allPresets: LoadState<List<PresetWithExercises>> = LoadState.Loading,
    val selectedPreset: PresetWithExercises? = null
)
