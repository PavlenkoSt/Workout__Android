package com.learning.workout__android.ui.viewmodel

import androidx.compose.foundation.pager.PagerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CalendarViewModelFactory(private val pagerState: PagerState, private val initialPage: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            return CalendarViewModel(pagerState, initialPage) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}