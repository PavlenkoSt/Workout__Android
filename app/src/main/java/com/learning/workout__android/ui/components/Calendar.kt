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
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.data.CalendarDataSource
import com.learning.workout__android.model.CalendarUiModel
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.ui.viewmodel.CalendarViewModel
import com.learning.workout__android.ui.viewmodel.CalendarViewModelFactory
import kotlinx.coroutines.launch
import java.time.LocalDate


@Composable
fun Calendar(
    modifier: Modifier = Modifier,
    onDateClick: (CalendarUiModel.Date) -> Unit,
    pagerState: PagerState,
    calendarUiModel: CalendarUiModel,
    initialPage: Int,
    initialWeekStart: LocalDate,
    title: String
) {
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier.animateContentSize()) {
        Header(
            title = title,
            data = calendarUiModel,
            onPrevClickListener = {
                coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
            },
            onNextClickListener = {
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

            val rowCalendar = CalendarDataSource.getData(
                startDate = weekStart,
                lastSelectedDate = calendarUiModel.selectedDate.date
            )

            DaysRow(
                data = rowCalendar,
                onDateClickListener = onDateClick,
                activeDate = calendarUiModel.selectedDate.date
            )
        }
    }
}

@Composable
private fun Header(
    title: String,
    modifier: Modifier = Modifier,
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
) {
    Row(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            style = MaterialTheme.typography.headlineSmall
        )
        IconButton(onClick = {
            onPrevClickListener(data.startDate.date)
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous"
            )
        }
        IconButton(onClick = {
            onNextClickListener(data.endDate.date)
        }) {
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
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
    activeDate: LocalDate
) {
    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        data.week.map { date ->
            Day(
                date = date,
                onClickListener = onDateClickListener,
                modifier = Modifier
                    .padding(vertical = 4.dp, horizontal = 2.dp)
                    .weight(1f),
                isActive = activeDate.toString() == date.date.toString()
            )
        }
    }
}

@Composable
fun Day(
    date: CalendarUiModel.Date,
    onClickListener: (CalendarUiModel.Date) -> Unit,
    modifier: Modifier = Modifier,
    isActive: Boolean
) {
    Card(
        modifier = modifier.clickable { onClickListener(date) },
        border = BorderStroke(
            width = 2.dp,
            color =  MaterialTheme.colorScheme.scrim
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
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = date.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if(date.isToday) {
            Text(
                text = "Today",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .background( color = if (isActive) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.secondary
                    })
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}


@Preview()
@Composable()
fun DayPreview () {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { Int.MAX_VALUE })
    val calendarViewModel: CalendarViewModel =
        viewModel(factory = CalendarViewModelFactory(pagerState, 0))

    val calendarUiModel by calendarViewModel.calendarUiModel.collectAsState()

    Workout__AndroidTheme {
        Calendar(
            onDateClick = {},
            initialWeekStart = LocalDate.now(),
            initialPage = 0,
            pagerState = pagerState,
            calendarUiModel = calendarUiModel,
            title = "This is title"
        )
    }
}