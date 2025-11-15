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
import com.learning.workout__android.data.models.TrainingExercise
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.TrainingDayWithExercises
import com.learning.workout__android.data.repositories.TrainingDayRepository
import com.learning.workout__android.utils.LoadState
import com.learning.workout__android.utils.formatExerciseType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

typealias Reducer<S> = (S) -> S

private inline fun <S, T> Flow<T>.toReducer(
    crossinline update: S.(T) -> S

): Flow<Reducer<S>> = distinctUntilChanged().map { v -> { s: S -> s.update(v) } }

class TrainingViewModel(
    private val trainingDayRepository: TrainingDayRepository
) : ViewModel() {
    // Calendar baseline: Monday of "today" week
    private val initialWeekStart: LocalDate =
        LocalDate.now().with(java.time.DayOfWeek.MONDAY)

    private val _visibleWeekStart = MutableStateFlow(initialWeekStart)

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    private val allTrainingDaysWithLoading: Flow<LoadState<List<TrainingDayWithExercises>>> =
        trainingDayRepository.getAllTrainingDays()
            .distinctUntilChanged()
            .map<List<TrainingDayWithExercises>, LoadState<List<TrainingDayWithExercises>>> {
                LoadState.Success(it)
            }

    private val allTrainingDays: Flow<List<TrainingDayWithExercises>> =
        allTrainingDaysWithLoading
            .filterIsInstance<LoadState.Success<List<TrainingDayWithExercises>>>()
            .map { it.data }

    private val currentDayFlow: Flow<TrainingDayWithExercises?> =
        combine(allTrainingDays, _selectedDate) { allDays, selectedDate ->
            allDays.firstOrNull { it.trainingDay.date == selectedDate.toString() }
        }.distinctUntilChanged()

    private val isLoadingFlow: Flow<Boolean> =
        allTrainingDaysWithLoading.map { it is LoadState.Loading }

    private val exerciseToEdit = MutableStateFlow<TrainingExercise?>(null)
    private val localReorderReducer = MutableStateFlow<Pair<TrainingExercise, TrainingExercise>?>(null)

    private val calendarUiFlow: Flow<CalendarUiModel> =
        combine(_visibleWeekStart, _selectedDate) { start, selected ->
            makeCalendarUi(start, selected)
        }

    private val titleFlow: Flow<String> =
        calendarUiFlow.map { ui ->
            val week = ui.week
            val start = week.first().date
            val end = week.last().date
            if (start.month == end.month) {
                "${start.month.name.lowercase().replaceFirstChar { it.titlecase() }} ${start.year}"
            } else {
                "${start.month.name.lowercase().replaceFirstChar { it.titlecase() }} & " +
                        "${
                            end.month.name.lowercase().replaceFirstChar { it.titlecase() }
                        } ${end.year}"
            }
        }

    private val calendarReducers = calendarUiFlow.toReducer<TrainingUiState, CalendarUiModel> {
        copy(calendar = it)
    }
    private val titleReducers = titleFlow.toReducer<TrainingUiState, String> {
        copy(title = it)
    }
    private val dateReducers = _selectedDate.toReducer<TrainingUiState, LocalDate> {
        copy(selectedDate = it)
    }
    private val allDaysReducers =
        allTrainingDays.toReducer<TrainingUiState, List<TrainingDayWithExercises>> {
            copy(allTrainingDays = it)
        }
    private val dayReducers = currentDayFlow.toReducer<TrainingUiState, TrainingDayWithExercises?> {
        copy(currentDay = it, currentDayStatistics = buildStats(it))
    }
    private val editReducers = exerciseToEdit.toReducer<TrainingUiState, TrainingExercise?> {
        copy(exerciseToEdit = it)
    }
    private val isLoadingReducer = isLoadingFlow.toReducer<TrainingUiState, Boolean> {
        copy(isLoading = it)
    }
    private val localReorderReducers =
        localReorderReducer.toReducer<TrainingUiState, Pair<TrainingExercise, TrainingExercise>?> {
            if (it == null) return@toReducer this
            val (from, to) = it
            // Update currentDay locally by swapping exercise orders
            val updatedDay = this.currentDay?.let { day ->
                val updatedExercises = day.exercises.map { exercise ->
                    when (exercise.id) {
                        from.id -> exercise.copy(order = to.order)
                        to.id -> exercise.copy(order = from.order)
                        else -> exercise
                    }
                }
                // Create new TrainingDayWithExercises with updated exercises
                TrainingDayWithExercises(
                    trainingDay = day.trainingDay,
                    exercises = updatedExercises
                )
            }
            copy(currentDay = updatedDay, currentDayStatistics = buildStats(updatedDay))
        }

    val uiState: StateFlow<TrainingUiState> =
        merge(
            calendarReducers,
            titleReducers,
            dateReducers,
            allDaysReducers,
            dayReducers,
            editReducers,
            isLoadingReducer,
            localReorderReducers
        )
            .scan(TrainingUiState()) { state, reduce ->
                reduce(state)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                TrainingUiState()
            )

    // Called by the Composable when pager page changes
    fun onWeekVisible(start: LocalDate) {
        _visibleWeekStart.value = start
        val ui = makeCalendarUi(start, _selectedDate.value)
        onDateSelected(ui.selectedDate.date) // always in that week ⇒ no crash
    }

    fun onDateSelected(date: LocalDate) {
        if (_selectedDate != date) {
            _selectedDate.value = date
        }
    }

    fun scrollToToday(): LocalDate {
        val today = LocalDate.now()
        val monday = today.with(java.time.DayOfWeek.MONDAY)
        _visibleWeekStart.value = monday
        onDateSelected(today)
        return monday
    }

    fun setExerciseToEdit(exerciseId: Long?) {
        if (exerciseId == null) {
            exerciseToEdit.value = null
            return
        }
        // Look up the exercise from the current state by ID to ensure we have the latest version
        val currentDay = uiState.value.currentDay
        val latestExercise = currentDay?.sortedExercises?.find { it.id == exerciseId }
        exerciseToEdit.value = latestExercise
    }

    fun addDefaultExercise(formResult: ExerciseDefaultFormResult) {
        viewModelScope.launch {
            val selectedDate = _selectedDate.value.toString()
            val exercise = TrainingExercise(
                trainingDayId = 0, // Will be set by repository
                name = formResult.name,
                reps = formResult.reps.toInt(),
                sets = formResult.sets.toInt(),
                rest = formResult.rest.toInt(),
                type = formResult.type,
                setsDone = 0
            )
            trainingDayRepository.addExerciseToDate(selectedDate, exercise)
        }
    }

    fun addLadderExercise(formResult: ExerciseLadderFormResult) {
        viewModelScope.launch {
            val selectedDate = _selectedDate.value.toString()
            val from = formResult.from.toInt()
            val to = formResult.to.toInt()
            val step = formResult.step.toInt()
            val rest = formResult.rest.toInt()

            // Generate exercises for each rung of the ladder
            val exercises = mutableListOf<TrainingExercise>()
            var currentReps = from
            while (currentReps <= to) {
                exercises.add(
                    TrainingExercise(
                        trainingDayId = 0, // Will be set by repository
                        name = formResult.name,
                        reps = currentReps,
                        sets = 1,
                        rest = rest,
                        type = ExerciseType.DYNAMIC,
                        setsDone = 0
                    )
                )
                currentReps += step
            }

            trainingDayRepository.addExercisesToDate(selectedDate, exercises)
        }
    }

    fun addSimpleExercise(formResult: ExerciseSimpleFormResult) {
        viewModelScope.launch {
            val selectedDate = _selectedDate.value.toString()

            val exercise = TrainingExercise(
                trainingDayId = 0, // Will be set by repository
                name = "",
                reps = 1,
                sets = 1,
                rest = 0,
                type = formResult.type,
                setsDone = 0
            )

            trainingDayRepository.addExerciseToDate(selectedDate, exercise)
        }
    }

    fun updateExercise(exercise: TrainingExercise) {
        viewModelScope.launch {
            trainingDayRepository.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: TrainingExercise) {
        viewModelScope.launch {
            trainingDayRepository.deleteExercise(exercise)
        }
    }

    fun reorderExercises(from: TrainingExercise, to: TrainingExercise) {
        // Update locally right away for smooth UI
        localReorderReducer.value = from to to

        viewModelScope.launch {
            if (from.order != to.order) {
                trainingDayRepository.reorderExercises(
                    fromExerciseId = from.id,
                    toExerciseId = to.id
                )
                // Clear local reorder after DB update completes
                localReorderReducer.value = null
            }
        }
    }

    fun deleteTrainingDay(selectedDate: LocalDate) {
        viewModelScope.launch {
            trainingDayRepository.deleteTrainingDayWithExercises(selectedDate.toString())
        }
    }

    // ---- Helpers ----

    private fun makeCalendarUi(start: LocalDate, selected: LocalDate): CalendarUiModel {
        val days = (0..6).map { start.plusDays(it.toLong()) }
        val today = LocalDate.now()

        val effectiveSelected =
            when {
                selected in days -> selected
                today in days -> today
                else -> start
            }

        val week = days.map { d ->
            CalendarUiModel.Date(
                date = d,
                isToday = d == today,
                isSelected = d == effectiveSelected
            )
        }

        val selectedDate = week.first { it.date == effectiveSelected } // safe now
        return CalendarUiModel(week = week, selectedDate = selectedDate)
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

private fun buildStats(day: TrainingDayWithExercises?): List<TrainingStatisticsItem> =
    day?.sortedExercises
        ?.groupBy { it.name }
        ?.map { (name, exercises) ->
            val exerciseType = exercises.first().type
            TrainingStatisticsItem(
                exerciseName = getStatItemName(name, exerciseType),
                exerciseType = exerciseType,
                repsToDo = exercises.sumOf { it.reps * it.sets },
                repsDone = exercises.sumOf { it.reps * it.setsDone }
            )
        } ?: emptyList()

private fun getStatItemName(name: String, exerciseType: ExerciseType): String {
    if (name.isNotEmpty()) return name
    return formatExerciseType(exerciseType.toString())
}

data class TrainingUiState(
    val title: String = "",
    val calendar: CalendarUiModel = CalendarUiModel.empty(),
    val selectedDate: LocalDate = LocalDate.now(),
    val allTrainingDays: List<TrainingDayWithExercises> = emptyList(),
    val currentDay: TrainingDayWithExercises? = null,
    val exerciseToEdit: TrainingExercise? = null,
    val currentDayStatistics: List<TrainingStatisticsItem> = emptyList(),
    val isLoading: Boolean = true
)

data class CalendarUiModel(
    val week: List<Date>,
    val selectedDate: Date
) {
    data class Date(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean
    ) {
        val day: String = date.format(DateTimeFormatter.ofPattern("E"))
    }

    companion object {
        fun empty(): CalendarUiModel {
            val d = LocalDate.now()
            val dd = Date(d, isSelected = true, isToday = true)
            return CalendarUiModel(listOf(dd), dd)
        }
    }
}

data class TrainingStatisticsItem(
    val exerciseName: String,
    val exerciseType: ExerciseType,
    val repsToDo: Int,
    val repsDone: Int
)
