package com.learning.workout__android.ui.viewmodel

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.learning.workout__android.data.CalendarDataSource
import com.learning.workout__android.model.CalendarUiModel
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarViewModel(
    private val pagerState: PagerState,
    private val initialPage: Int
) : ViewModel() {
    val initialWeekStart: LocalDate = CalendarDataSource.today.with(DayOfWeek.MONDAY)

    private val currentWeekOffset = pagerState.currentPage - initialPage
    private val currentWeekStart: LocalDate = initialWeekStart.plusWeeks(currentWeekOffset.toLong())

    var calendarUiModel by mutableStateOf(
        CalendarDataSource.getData(
            startDate = currentWeekStart,
            lastSelectedDate = CalendarDataSource.today
        )
    )

    val currentMonth by derivedStateOf {
        val weekStart =
            initialWeekStart.plusWeeks((pagerState.currentPage - initialPage).toLong())
        val weekEnd = weekStart.plusDays(6)
        if (weekStart.month == weekEnd.month) {
            weekStart.month.toString().forceCapitalize()
        } else {
            "${weekStart.month.toString().forceCapitalize()} & ${
                weekEnd.month.toString().forceCapitalize()
            }"
        }
    }

    fun selectDayInWeek() {
        val newWeekStart =
            initialWeekStart.plusWeeks((pagerState.currentPage - initialPage).toLong())
        val newWeekDates =
            CalendarDataSource.getData(newWeekStart, calendarUiModel.selectedDate.date)

        val newSelectedDate =
            newWeekDates.week.firstOrNull { it.isToday } ?: newWeekDates.week.first()
        calendarUiModel = newWeekDates.copy(selectedDate = newSelectedDate)
    }

    fun onDateClick(date: CalendarUiModel.Date) {
        calendarUiModel = calendarUiModel.copy(
            selectedDate = date,
            week = calendarUiModel.week.map {
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