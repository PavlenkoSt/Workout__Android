package com.learning.workout__android.ui.viewmodel

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learning.workout__android.data.dataSources.CalendarDataSource
import com.learning.workout__android.model.CalendarUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarViewModel(
    private val pagerState: PagerState,
    private val initialPage: Int
) : ViewModel() {
    val initialWeekStart: LocalDate = CalendarDataSource.today.with(DayOfWeek.MONDAY)

    private val currentWeekOffset = pagerState.currentPage - initialPage
    private val currentWeekStart: LocalDate = initialWeekStart.plusWeeks(currentWeekOffset.toLong())

    private val _calendarUiModel = MutableStateFlow(
        CalendarDataSource.getData(
            startDate = currentWeekStart,
            lastSelectedDate = CalendarDataSource.today
        )
    )
    val calendarUiModel: StateFlow<CalendarUiModel> = _calendarUiModel.asStateFlow()

    val currentMonth = calendarUiModel.map { uiModel ->
        val weekStart = uiModel.week.first().date
        val weekEnd = weekStart.plusDays(6)
        if (weekStart.month == weekEnd.month) {
            weekStart.month.toString().forceCapitalize()
        } else {
            "${weekStart.month.toString().forceCapitalize()} & ${
                weekEnd.month.toString().forceCapitalize()
            }"
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    fun selectDayInWeek() {
        val newWeekStart =
            initialWeekStart.plusWeeks((pagerState.currentPage - initialPage).toLong())
        val newWeekDates =
            CalendarDataSource.getData(newWeekStart, _calendarUiModel.value.selectedDate.date)

        val newSelectedDate =
            newWeekDates.week.firstOrNull { it.isToday } ?: newWeekDates.week.first()
        _calendarUiModel.value = newWeekDates.copy(selectedDate = newSelectedDate)
    }

    fun onDateClick(date: CalendarUiModel.Date) {
        _calendarUiModel.value = _calendarUiModel.value.copy(
            selectedDate = date,
            week = _calendarUiModel.value.week.map {
                it.copy(
                    isSelected = it.date.isEqual(date.date)
                )
            }
        )
    }

    suspend fun scrollToToday() {
        val today = LocalDate.now()

        val todayWeekOffset =
            java.time.temporal.ChronoUnit.WEEKS.between(initialWeekStart, today)
        val todayPage = (initialPage + todayWeekOffset).toInt()

        pagerState.animateScrollToPage(todayPage)
        onDateClick(
            CalendarUiModel.Date(
                today,
                isSelected = true,
                isToday = true
            )
        )
    }
}

fun String.forceCapitalize(): String {
    return this
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}