package com.learning.workout__android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.ui.components.Calendar
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatDate
import com.learning.workout__android.viewModel.TrainingViewModel
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate

@Composable
fun TrainingScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    val vm: TrainingViewModel = viewModel(
        factory = TrainingViewModel.provideFactory(LocalContext.current)
    )
    val ui by vm.uiState.collectAsState()

    val initialPage = Int.MAX_VALUE / 2
    val initialWeekStart = remember { LocalDate.now().with(java.time.DayOfWeek.MONDAY) }
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { Int.MAX_VALUE })

    LaunchedEffect(pagerState.currentPage) {
        val weeksFromStart = pagerState.currentPage - initialPage
        val start =
            LocalDate.now().with(java.time.DayOfWeek.MONDAY).plusWeeks(weeksFromStart.toLong())
        vm.onWeekVisible(start)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column {
            Calendar(
                modifier = Modifier.fillMaxWidth(),
                onDateClick = { date -> vm.onDateSelected(date.date) },
                pagerState = pagerState,
                calendarUiModel = ui.calendar,
                initialWeekStart = initialWeekStart,
                initialPage = initialPage,
                title = ui.title,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Header(
                currentDate = ui.selectedDate,
                modifier = Modifier.fillMaxWidth()
            )

            ui.currentDay?.let { day ->
                TrainingExerciseList(exercisesList = ui.currentDay?.exercises ?: emptyList())
            }
        }

        if (!ui.calendar.selectedDate.isToday) {
            TodayFloatBtn(
                onClick = {
                    val monday = vm.scrollToToday()
                    // jump pager to that week
                    val base = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
                    val deltaWeeks = java.time.temporal.ChronoUnit.WEEKS.between(
                        base, monday
                    ).toInt()
                    val target = initialPage + deltaWeeks
                    // animate
                    coroutineScope.launch { pagerState.animateScrollToPage(target) }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun Header(
    currentDate: LocalDate,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        Text(
            text = "Workout session - ${formatDate(currentDate)}",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TodayFloatBtn(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        shape = ShapeDefaults.Large,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, end = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = null,
                modifier = Modifier.rotate(90F)
            )
            Text(text = "Today")
        }
    }
}

@Composable
private fun TrainingExerciseList(
    exercisesList: List<Exercise>
) {
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Update the list
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(all = 8.dp)
    ) {
        itemsIndexed(exercisesList, key = { _, item -> item.id }) { idx, item ->
            ReorderableItem(reorderableLazyListState, key = item.id) { isDragging ->
                ExerciseItem(
                    exercise = item,
                    modifier = Modifier
                        .draggableHandle()
                        .fillMaxWidth(),
                    idx = idx
                )
            }
        }
    }
}

@Composable
private fun ExerciseItem(
    exercise: Exercise,
    idx: Int,
    modifier: Modifier = Modifier
) {
    Column {
        Card(modifier = modifier) {
            Text(
                text = "${idx + 1}. ${exercise.name}",
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 8.dp,
                    end = 8.dp
                )
            )
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = ShapeDefaults.Medium
                ) {
                    Text("-")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier.width(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("0/1")
                    LinearProgressIndicator(
                        progress = { 0.5f },
                        modifier = Modifier.fillMaxWidth(),
                        trackColor = MaterialTheme.colorScheme.onPrimary,
                        color = MaterialTheme.colorScheme.primary,
                        strokeCap = StrokeCap.Round,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {},
                    modifier = Modifier.weight(1f),
                    shape = ShapeDefaults.Medium
                ) {
                    Text("+")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
@Preview
fun TrainingScreenPreview() {
    Workout__AndroidTheme {
        TrainingScreen()
    }
}
