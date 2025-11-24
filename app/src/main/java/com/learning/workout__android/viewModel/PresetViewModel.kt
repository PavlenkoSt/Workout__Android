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
import com.learning.workout__android.data.models.TrainingDay
import com.learning.workout__android.data.models.TrainingDayWithExercises
import com.learning.workout__android.data.models.TrainingExercise
import com.learning.workout__android.data.repositories.PresetsRepository
import com.learning.workout__android.data.repositories.TrainingDayRepository
import com.learning.workout__android.utils.LoadState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

typealias PresetReducer<S> = (S) -> S

private inline fun <S, T> Flow<T>.toPresetReducer(
    crossinline update: S.(T) -> S
): Flow<PresetReducer<S>> = distinctUntilChanged().map { v -> { s: S -> s.update(v) } }

class PresetViewModel(
    private val presetsRepository: PresetsRepository,
    private val trainingDayRepository: TrainingDayRepository,
    private val presetId: Long
) : ViewModel() {
    private val targetPresetFlow: Flow<PresetWithExercises?> =
        presetsRepository.getPresetByIdWithExercises(presetId).distinctUntilChanged()

    val trainingDayDates = trainingDayRepository.getTrainingDaysDates().distinctUntilChanged()

    private val _exerciseToEdit = MutableStateFlow<PresetExercise?>(null)

    private val _localReorderReducer =
        MutableStateFlow<Pair<PresetExercise, PresetExercise>?>(null)

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
        // Update locally right away for smooth UI
        _localReorderReducer.value = from to to

        viewModelScope.launch {
            if (from.order != to.order) {
                presetsRepository.reorderExercises(
                    fromExerciseId = from.id,
                    toExerciseId = to.id,
                )
                // Clear local reorder after DB update completes
                _localReorderReducer.value = null
            }
        }
    }

    fun usePreset(date: String) {
        viewModelScope.launch {
            val trainingDay = trainingDayRepository.getTrainingDayByDate(date).first()
            if (trainingDay != null) return@launch

            val targetExercises = targetPresetFlow.filterIsInstance<PresetWithExercises>()
                .map { it.exercises }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
                .value

            val presetExercises = targetExercises.map {
                TrainingExercise(
                    name = it.name,
                    reps = it.reps,
                    rest = it.rest,
                    sets = it.sets,
                    type = it.type,
                    order = it.order,
                    setsDone = 0,
                    trainingDayId = 0 // will be set automatically
                )
            }

            trainingDayRepository.addTrainingDayWithExercises(
                TrainingDayWithExercises(
                    trainingDay = TrainingDay(date = date),
                    exercises = presetExercises
                )
            )
        }
    }

    private val presetReducers =
        targetPresetFlow.toPresetReducer<PresetUiState, PresetWithExercises?> {
            copy(preset = LoadState.Success(it ?: return@toPresetReducer this))
        }

    private val editReducers = _exerciseToEdit.toPresetReducer<PresetUiState, PresetExercise?> {
        copy(exerciseToEdit = it)
    }

    private val localReorderReducers =
        _localReorderReducer.toPresetReducer<PresetUiState, Pair<PresetExercise, PresetExercise>?> {
            if (it == null) return@toPresetReducer this
            val (from, to) = it
            // Update preset locally by swapping exercise orders
            val updatedPreset = this.preset.let { state ->
                when (state) {
                    is LoadState.Success -> {
                        val updatedExercises = state.data.exercises.map { exercise ->
                            when (exercise.id) {
                                from.id -> exercise.copy(order = to.order)
                                to.id -> exercise.copy(order = from.order)
                                else -> exercise
                            }
                        }
                        LoadState.Success(state.data.copy(exercises = updatedExercises))
                    }

                    else -> state
                }
            }
            copy(preset = updatedPreset)
        }

    private val trainingDayDatesReducers =
        trainingDayDates.toPresetReducer<PresetUiState, List<String>> {
            copy(trainingDayDates = it)
        }

    val uiState: StateFlow<PresetUiState> =
        merge(
            presetReducers,
            editReducers,
            localReorderReducers,
            trainingDayDatesReducers
        )
            .scan(PresetUiState()) { state, reduce ->
                reduce(state)
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
                    val trainingDayRepository =
                        TrainingDayRepository(trainingDayDao = db.trainingDayDao())

                    PresetViewModel(presetsRepository, trainingDayRepository, presetId)
                }
            }
    }
}

data class PresetUiState(
    val preset: LoadState<PresetWithExercises> = LoadState.Loading,
    val exerciseToEdit: PresetExercise? = null,
    val trainingDayDates: List<String> = emptyList()
)