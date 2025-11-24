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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

typealias PresetsReducer<S> = (S) -> S

private inline fun <S, T> Flow<T>.toPresetsReducer(
    crossinline update: S.(T) -> S
): Flow<PresetsReducer<S>> = distinctUntilChanged().map { v -> { s: S -> s.update(v) } }

class PresetsViewModel(
    private val presetsRepository: PresetsRepository
) : ViewModel() {
    private val allPresetsFlow = presetsRepository.getAllPresets().distinctUntilChanged()

    private val _localReorderReducer =
        MutableStateFlow<Pair<PresetWithExercises, PresetWithExercises>?>(null)

    private val _search = MutableStateFlow("")

    private val _presetToEdit = MutableStateFlow<PresetWithExercises?>(null)

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
        val presetToUpdate = _presetToEdit.value?.preset
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
        // Update locally right away for smooth UI
        _localReorderReducer.value = from to to

        viewModelScope.launch {
            if (from.preset.order != to.preset.order) {
                presetsRepository.reorderPresets(
                    fromPresetId = from.preset.id,
                    toPresetId = to.preset.id,
                )
                // Clear local reorder after DB update completes
                _localReorderReducer.value = null
            }
        }
    }

    private val presetsReducers =
        allPresetsFlow.toPresetsReducer<PresetsUiState, List<PresetWithExercises>> {
            copy(allPresets = LoadState.Success(it))
        }

    private val reorderReducers =
        _localReorderReducer.toPresetsReducer<PresetsUiState, Pair<PresetWithExercises, PresetWithExercises>?> {
            if (it == null) return@toPresetsReducer this
            val (from, to) = it
            // Update presets locally by swapping preset orders
            val updatedState = this.allPresets.let { state ->
                when (state) {
                    is LoadState.Success -> {
                        val updatedPresets = state.data.map { item ->
                            val updatedPreset = when (item.preset.id) {
                                from.preset.id -> item.preset.copy(order = to.preset.order)
                                to.preset.id -> item.preset.copy(order = from.preset.order)
                                else -> item.preset
                            }
                            item.copy(preset = updatedPreset)
                        }.sortedByDescending { it.preset.order }
                        LoadState.Success(updatedPresets)
                    }

                    else -> state
                }
            }
            copy(allPresets = updatedState)
        }

    private val searchReducers = _search.toPresetsReducer<PresetsUiState, String> {
        copy(search = it)
    }

    private val editReducers =
        _presetToEdit.toPresetsReducer<PresetsUiState, PresetWithExercises?> {
            copy(presetToEdit = it)
        }

    val uiState: StateFlow<PresetsUiState> =
        merge(
            presetsReducers,
            reorderReducers,
            searchReducers,
            editReducers
        )
            .scan(PresetsUiState()) { state, reduce ->
                reduce(state)
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
    val search: String = "",
    val presetToEdit: PresetWithExercises? = null
) {
    fun getFilteredAndSortedPresets(): List<PresetWithExercises> =
        when (allPresets) {
            is LoadState.Success -> allPresets.data
                .filter { it.preset.name.contains(search.trim(), true) }

            else -> emptyList()
        }
}