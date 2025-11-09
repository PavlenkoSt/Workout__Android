package com.learning.workout__android.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.learning.workout__android.data.AppDatabase
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.TrainingDayWithExercises
import com.learning.workout__android.data.repositories.TrainingDayRepository
import com.learning.workout__android.ui.components.ExerciseDefaultFormResult
import com.learning.workout__android.ui.components.ExerciseLadderFormResult
import com.learning.workout__android.ui.components.ExerciseSimpleFormResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrainingViewModel(
    private val trainingDayRepository: TrainingDayRepository
) : ViewModel() {
    // Calendar baseline: Monday of "today" week
    private val initialWeekStart: LocalDate =
        LocalDate.now().with(java.time.DayOfWeek.MONDAY)

    // UI-driven: currently visible week start (fed by pager)
    private val _visibleWeekStart = MutableStateFlow(initialWeekStart)

    // Selected date lives here (authoritative)
    private val _selectedDate = MutableStateFlow(LocalDate.now())

    // All days for list; and current day for selected date
    private val allDaysFlow = trainingDayRepository.getAll() // Flow<List<TrainingDay>>

    @OptIn(ExperimentalCoroutinesApi::class)
    private val selectedDayFlow: Flow<TrainingDayWithExercises?> =
        _selectedDate.flatMapLatest { date ->
            trainingDayRepository.getByDate(date.toString())
        }

    // Calendar model for the visible week (computed here, no separate VM needed)
    private val calendarUiFlow: Flow<CalendarUiModel> =
        combine(_visibleWeekStart, _selectedDate) { start, selected ->
            makeCalendarUi(start, selected)
        }

    // Title like "October & November 2025"
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

    // Single UI state the screen observes
    val uiState: StateFlow<TrainingUiState> =
        combine(
            calendarUiFlow,
            titleFlow,
            _selectedDate,
            allDaysFlow,
            selectedDayFlow
        ) { calendarUi, title, selected, allDays, currentDay ->
            TrainingUiState(
                title = title,
                calendar = calendarUi,
                selectedDate = selected,
                trainingDays = allDays,
                currentDay = currentDay
            )
        }.stateIn(viewModelScope, SharingStarted.Eagerly, TrainingUiState())

    // Called by the Composable when pager page changes
    fun onWeekVisible(start: LocalDate) {
        _visibleWeekStart.value = start
        val ui = makeCalendarUi(start, _selectedDate.value)
        onDateSelected(ui.selectedDate.date) // always in that week ⇒ no crash
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun scrollToToday(): LocalDate {
        val today = LocalDate.now()
        val monday = today.with(java.time.DayOfWeek.MONDAY)
        _visibleWeekStart.value = monday
        onDateSelected(today)
        return monday
    }

    fun addDefaultExercise(formResult: ExerciseDefaultFormResult) {
        viewModelScope.launch {
            val selectedDate = _selectedDate.value.toString()
            val exercise = Exercise(
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
            val exercises = mutableListOf<Exercise>()
            var currentReps = from
            while (currentReps <= to) {
                exercises.add(
                    Exercise(
                        trainingDayId = 0, // Will be set by repository
                        name = formResult.name,
                        reps = currentReps,
                        sets = 1,
                        rest = rest,
                        type = formResult.type,
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

            val exercise = Exercise(
                trainingDayId = 0, // Will be set by repository
                name = formResult.type.toString(),
                reps = 1,
                sets = 1,
                rest = 0,
                type = formResult.type,
                setsDone = 0
            )

            trainingDayRepository.addExerciseToDate(selectedDate, exercise)
        }
    }

    fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            trainingDayRepository.updateExercise(exercise)
        }
    }

    fun deleteExercise(exercise: Exercise) {
        viewModelScope.launch {
            trainingDayRepository.deleteExercise(exercise)
        }
    }

    fun reorderExercises(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentDay = trainingDayRepository.getByDate(_selectedDate.value.toString()).first()
            if (currentDay != null && fromIndex != toIndex) {
                trainingDayRepository.reorderExercises(
                    trainingDayId = currentDay.trainingDay.id,
                    fromIndex = fromIndex,
                    toIndex = toIndex
                )
            }
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

data class TrainingUiState(
    val title: String = "",
    val calendar: CalendarUiModel = CalendarUiModel.empty(),
    val selectedDate: LocalDate = LocalDate.now(),
    val trainingDays: List<TrainingDayWithExercises> = emptyList(),
    val currentDay: TrainingDayWithExercises? = null
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