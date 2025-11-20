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

    private var _search = MutableStateFlow("")
    val search: StateFlow<String> = _search

    private var _presetToEdit = MutableStateFlow<PresetWithExercises?>(null)
    val presetToEdit = _presetToEdit

    fun onSearch(value: String) {
        _search.value = value
    }

    fun setPresetToEdit(preset: PresetWithExercises?) {
        _presetToEdit.value = preset
    }

    fun createPreset(name: String) {
        viewModelScope.launch {
            presetsRepository.createPreset(Preset(name = name, order = System.currentTimeMillis()))
        }
    }

    fun updatePreset(name: String) {
        val presetToUpdate = presetToEdit.value?.preset
        if (presetToUpdate != null) {
            viewModelScope.launch {
                presetsRepository.updatePreset(
                    presetToUpdate.copy(
                        name = name
                    )
                )
            }
        }
    }

    fun deletePreset(preset: Preset) {
        viewModelScope.launch {
            presetsRepository.deletePreset(preset)
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
            _localSwap,
            _search,
            _presetToEdit
        ) { presets, swap, search, presetToEdit ->
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
            }.sortedByDescending { it.preset.order }

            PresetsUiState(
                allPresets = LoadState.Success(displayList.filter {
                    it.preset.name.contains(
                        search.trim(),
                        true
                    )
                }),
                presetToEdit = presetToEdit
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
    val presetToEdit: PresetWithExercises? = null
)
