package com.learning.workout__android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.Preset
import com.learning.workout__android.data.models.PresetWithExercises
import com.learning.workout__android.data.repositories.PresetsRepository
import com.learning.workout__android.utils.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PresetsViewModel(
    private val presetsRepository: PresetsRepository
) : ViewModel() {
    val allPresets = presetsRepository.getAllPresets().distinctUntilChanged()

    private val _localSwap =
        MutableStateFlow<Pair<PresetWithExercises, PresetWithExercises>?>(null)

    private var _selectedPresetId = MutableStateFlow<Long?>(null)
    val selectedPresetId: StateFlow<Long?> = _selectedPresetId

    private var _search = MutableStateFlow("")
    val search: StateFlow<String> = _search

    fun selectPresetId(presetId: Long?) {
        _selectedPresetId.value = presetId
    }

    fun onSearch(value: String) {
        _search.value = value
    }

    fun createPreset(name: String) {
        viewModelScope.launch {
            presetsRepository.createPreset(Preset(name = name, order = System.currentTimeMillis()))
        }
    }


    fun reorderPresets(from: PresetWithExercises, to: PresetWithExercises) {
        _localSwap.value = from to to  // optimistic

        viewModelScope.launch {
            if (from.preset.order != to.preset.order) {
                presetsRepository.reorderPresets(
                    fromPresetId = from.preset.id,
                    toPresetId = to.preset.id,
                )
            }
            _localSwap.value = null      // clear when done
        }
    }

    val uiState: StateFlow<PresetsUiState> =
        combine(
            allPresets,
            selectedPresetId,
            _localSwap
        ) { presets, selectedId, swap ->
            val baseList = presets

            val displayList = if (swap != null) {
                val (from, to) = swap

                baseList.map { item ->
                    val updatedPreset = when (item.preset.id) {
                        from.preset.id -> item.preset.copy(order = to.preset.order)
                        to.preset.id -> item.preset.copy(order = from.preset.order)
                        else -> item.preset
                    }
                    item.copy(preset = updatedPreset)
                }
            } else {
                baseList
            }.sortedBy { it.preset.order } // if you rely on order

            PresetsUiState(
                allPresets = LoadState.Success(displayList),
                selectedPreset = selectedId?.let { id ->
                    displayList.find { it.preset.id == id }
                }
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
