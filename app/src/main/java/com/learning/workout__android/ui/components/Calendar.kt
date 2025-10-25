package com.learning.workout__android.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.dataSources.CalendarDataSource
import com.learning.workout__android.viewModel.CalendarUiModel
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    title: String,
    calendarUiModel: CalendarUiModel,
    pagerState: PagerState,
    initialPage: Int,
    initialWeekStart: LocalDate,
    onDateClick: (CalendarUiModel.Date) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.animateContentSize()) {
        Header(
            title = title,
            onPrevClick = {
                coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            },
            onNextClick = {
                coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )

        HorizontalPager(
            state = pagerState,
            flingBehavior = PagerDefaults.flingBehavior(
                state = pagerState,
                snapPositionalThreshold = 0.2f
            )
        ) { page ->
            val weekOffset = page - initialPage
            val weekStart = initialWeekStart.plusWeeks(weekOffset.toLong())

            // Build the row model for this page, highlighting the VM’s selected day
            val rowCalendar = CalendarDataSource.getData(
                startDate = weekStart,
                lastSelectedDate = calendarUiModel.selectedDate.date
            )

            DaysRow(
                data = rowCalendar,
                onDateClick = onDateClick,
                activeDate = calendarUiModel.selectedDate.date
            )
        }
    }
}

@Composable
private fun Header(
    title: String,
    modifier: Modifier = Modifier,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.headlineSmall
        )
        IconButton(onClick = onPrevClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous"
            )
        }
        IconButton(onClick = onNextClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next"
            )
        }
    }
}

@Composable
fun DaysRow(
    data: CalendarUiModel,
    onDateClick: (CalendarUiModel.Date) -> Unit,
    activeDate: LocalDate
) {
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        data.week.forEach { date ->
            Day(
                date = date,
                onClick = onDateClick,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 2.dp)
                    .weight(1f),
                isActive = activeDate == date.date // compare LocalDate directly
            )
        }
    }
}

@Composable
fun Day(
    date: CalendarUiModel.Date,
    onClick: (CalendarUiModel.Date) -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean
) {
    Card(
        modifier = modifier.clickable { onClick(date) },
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.scrim
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primary
            }
        ),
    ) {
        Column(
            modifier = Modifier
                .height(56.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.day,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = date.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (date.isToday) {
            Text(
                text = "Today",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background(
                        if (isActive) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.secondary
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}