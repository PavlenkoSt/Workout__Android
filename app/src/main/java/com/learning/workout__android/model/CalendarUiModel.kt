package com.learning.workout__android.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class CalendarUiModel(
    val selectedDate: Date,
    val week: List<Date>
) {

    val startDate: Date = week.first()
    val endDate: Date = week.last()

    data class Date(
        val date: LocalDate,
        val isSelected: Boolean,
        val isToday: Boolean
    ) {
        val day: String = date.format(DateTimeFormatter.ofPattern("E"))
    }
}