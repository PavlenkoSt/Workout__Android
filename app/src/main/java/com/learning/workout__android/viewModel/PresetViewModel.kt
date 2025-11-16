package com.learning.workout__android.viewModel

import ExerciseDefaultFormResult
import ExerciseLadderFormResult
import ExerciseSimpleFormResult
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.PresetExercise
import com.learning.workout__android.data.models.PresetWithExercises
import com.learning.workout__android.data.repositories.PresetsRepository
import com.learning.workout__android.utils.LoadState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class PresetViewModel(
    private val presetsRepository: PresetsRepository,
    private val presetId: Long
) : ViewModel() {
    val targetPreset = presetsRepository.getPresetByIdWithExercises(presetId).distinctUntilChanged()

    private val _localSwap =
        MutableStateFlow<Pair<PresetExercise, PresetExercise>?>(null)

    private var _exerciseToEdit = MutableStateFlow<PresetExercise?>(null)
    val exerciseToEdit = _exerciseToEdit.value

    fun setExerciseToEdit(exercise: PresetExercise?) {
        _exerciseToEdit.value = exercise
    }

    fun addDefaultExercise(formResult: ExerciseDefaultFormResult) {
        viewModelScope.launch {
            val exercise = PresetExercise(
                name = formResult.name,
                reps = formResult.reps.toInt(),
                sets = formResult.sets.toInt(),
                rest = formResult.rest.toInt(),
                type = formResult.type,
                presetId = presetId,
            )

            presetsRepository.addExercise(exercise)
        }
    }

    fun addLadderExercise(formResult: ExerciseLadderFormResult) {
        viewModelScope.launch {
            val from = formResult.from.toInt()
            val to = formResult.to.toInt()
            val step = formResult.step.toInt()
            val rest = formResult.rest.toInt()

            // Generate exercises for each rung of the ladder
            var currentReps = from
            while (currentReps <= to) {
                val exercise = PresetExercise(
                    name = formResult.name,
                    reps = currentReps,
                    sets = 1,
                    rest = rest,
                    type = ExerciseType.DYNAMIC,
                    presetId = presetId,
                )
                currentReps += step

                presetsRepository.addExercise(exercise)
            }
        }
    }

    fun addSimpleExercise(formResult: ExerciseSimpleFormResult) {
        viewModelScope.launch {
            val exercise = PresetExercise(
                name = "",
                reps = 1,
                sets = 1,
                rest = 0,
                type = formResult.type,
                presetId = presetId,
            )

            presetsRepository.addExercise(exercise)
        }
    }

    fun updateExerciseInPreset(exercise: PresetExercise) {
        viewModelScope.launch {
            presetsRepository.updateExercise(exercise)
        }
    }

    fun deleteExerciseFromPreset(exercise: PresetExercise) {
        viewModelScope.launch {
            presetsRepository.deleteExercise(exercise)
        }
    }

    fun reorderExercises(from: PresetExercise, to: PresetExercise) {
        _localSwap.value = from to to  // optimistic

        viewModelScope.launch {
            if (from.order != to.order) {
                presetsRepository.reorderExercises(
                    fromExerciseId = from.id,
                    toExerciseId = to.id,
                )
            }
            _localSwap.value = null
        }
    }

    val uiState = combine(
        targetPreset,
        _exerciseToEdit,
        _localSwap
    ) { preset, exerciseToEdit, swap ->
        val loadState: LoadState<PresetWithExercises> =
            if (preset == null) {
                LoadState.Loading
            } else {
                val exercises = if (swap != null) {
                    val (from, to) = swap

                    preset.exercises.map { exercise ->
                        when (exercise.id) {
                            from.id -> exercise.copy(order = to.order)
                            to.id -> exercise.copy(order = from.order)
                            else -> exercise
                        }
                    }
                } else {
                    preset.exercises
                }

                LoadState.Success(preset.copy(exercises = exercises))
            }

        PresetUiState(preset = loadState, exerciseToEdit = exerciseToEdit)
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PresetUiState()
        )

    companion object {
        fun provideFactory(context: Context, presetId: Long): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    val db = AppDatabase.getDatabase(context)
                    val presetsRepository = PresetsRepository(presetDao = db.presetDao())

                    PresetViewModel(presetsRepository, presetId)
                }
            }
    }
}

data class PresetUiState(
    val preset: LoadState<PresetWithExercises> = LoadState.Loading,
    val exerciseToEdit: PresetExercise? = null
)
