package com.learning.workout__android.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.R
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.ui.components.Calendar
import com.learning.workout__android.ui.components.ExerciseForm
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatDate
import com.learning.workout__android.viewModel.TrainingViewModel
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
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

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

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

            TrainingExerciseList(
                exercisesList = ui.currentDay?.sortedExercises ?: emptyList(),
                onReorder = { from, to -> vm.reorderExercises(from, to) },
                footer = {
                    Footer(
                        text = if (ui.currentDay != null) { "+ Add exercise" } else { "Create training" },
                        onClick = { showBottomSheet = true })
                },
                header = {
                    Header(
                        currentDate = ui.selectedDate,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            )
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

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {  showBottomSheet = false  },
            sheetState = sheetState
        ) {
            ExerciseForm(
                onDefaultExerciseSubmit = { result ->
                    vm.addDefaultExercise(result)
                    showBottomSheet = false
                },
                onLadderExerciseSubmit = { result ->
                    vm.addLadderExercise(result)
                    showBottomSheet = false
                },
                onSimpleExerciseSubmit = { result ->
                    vm.addSimpleExercise(result)
                    showBottomSheet = false
                },
                exerciseToEdit = null
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
private fun Footer(
    text: String,
    onClick: () -> Unit
){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = onClick) {
            Text(text = text)
        }
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
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = null,
                modifier = Modifier.rotate(90F)
            )
            Text(text = "Today")
        }
    }
}

@Composable
private fun TrainingExerciseList(
    exercisesList: List<Exercise>,
    onReorder: (from: Int, to: Int) -> Unit,
    footer: @Composable () -> Unit,
    header: @Composable () -> Unit
) {
    // Local state for optimistic updates to prevent flickering
    val localExercises = remember { mutableStateListOf<Exercise>() }
    val onReorderCallback = rememberUpdatedState(onReorder)
    val coroutineScope = rememberCoroutineScope()
    var isReordering by remember { mutableStateOf(false) }
    
    // Update local state when the source list changes (but not during reordering)
    LaunchedEffect(exercisesList) {
        if (!isReordering) {
            // Only update if the lists are actually different to avoid unnecessary updates
            if (localExercises.size != exercisesList.size || 
                localExercises.zip(exercisesList).any { (a, b) -> a.id != b.id || a.order != b.order }) {
                localExercises.clear()
                localExercises.addAll(exercisesList)
            }
        }
    }
    
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // Adjust indices to account for header item (header is at index 0)
        val adjustedFrom = from.index - 1 // Subtract 1 for header
        val adjustedTo = to.index - 1 // Subtract 1 for header
        
        if (adjustedFrom in localExercises.indices && adjustedTo in 0..localExercises.size) {
            // Optimistic update: immediately update local state
            isReordering = true
            val item = localExercises.removeAt(adjustedFrom)
            localExercises.add(adjustedTo, item)
            
            // Persist to database after a short delay to let animation complete
            coroutineScope.launch {
                delay(150) // Small delay to let animation finish
                onReorderCallback.value(adjustedFrom, adjustedTo)
                delay(100) // Wait a bit more for DB update to complete
                isReordering = false
            }
        }
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(all = 8.dp)
    ) {
        item(key = "header") {
            header()
        }

        itemsIndexed(localExercises, key = { _, item -> item.id }) { idx, item ->
            ReorderableItem(reorderableLazyListState, key = item.id) { isDragging ->
                ExerciseItem(
                    exercise = item,
                    idx = idx,
                    draggableHandler = {
                        DraggableHandler(
                            modifier = Modifier.draggableHandle()
                        )
                    }
                )
            }
        }

        item(key = "footer") {
            footer()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseItem(
    exercise: Exercise,
    idx: Int,
    draggableHandler: @Composable () ->  Unit
) {
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                // TODO remove
                print("remove")
            } else if (it == SwipeToDismissBoxValue.StartToEnd) {
                // TODO edit
                print("edit")
            }
            it != SwipeToDismissBoxValue.StartToEnd
        },
        positionalThreshold = { totalDistance -> totalDistance * 0.35f }
    )

    Column {
        SwipeToDismissBox(
            state = swipeToDismissBoxState,
            modifier = Modifier.fillMaxWidth(),
            backgroundContent = {
                when (swipeToDismissBoxState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd  -> {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit exercise",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(ShapeDefaults.Medium)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 24.dp)
                                .wrapContentSize(Alignment.CenterStart)
                               ,
                            tint = Color.White
                        )
                    }
                    SwipeToDismissBoxValue.EndToStart -> {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove exercise",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(ShapeDefaults.Medium)
                                .background(Color.Red)
                                .padding(horizontal = 24.dp)
                                .wrapContentSize(Alignment.CenterEnd),
                            tint = Color.White
                        )
                    }
                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "${idx + 1}. ${exercise.name}",
                        modifier = Modifier.padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp
                        )
                    )
                    if(
                        exercise.type == ExerciseType.DYNAMIC ||
                        exercise.type == ExerciseType.STATIC ||
                        exercise.type == ExerciseType.LADDER
                        ) {
                        when(exercise.type) {
                            ExerciseType.DYNAMIC -> ExerciseStatItem("Reps:", exercise.reps.toString())
                            ExerciseType.LADDER -> ExerciseStatItem("Reps:", exercise.reps.toString())
                            ExerciseType.STATIC -> ExerciseStatItem("Hold:", "${exercise.reps} sec.")
                            else -> null
                        }
                        ExerciseStatItem("Sets:", exercise.sets.toString())
                        ExerciseStatItem("Rest:", "${exercise.rest} sec.")
                    }
                    draggableHandler()
                }
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
                        Text("${exercise.setsDone}/${exercise.sets}")
                        LinearProgressIndicator(
                            progress = { ((exercise.setsDone / exercise.sets)).toFloat() },
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
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ExerciseStatItem(
    stat: String,
    value: String
) {
    Column (
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(stat, fontSize = 12.sp, lineHeight = 14.sp)
        Text(value, fontSize = 12.sp, lineHeight = 14.sp)
    }
}

@Composable
private fun DraggableHandler (modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.hand),
        contentDescription = "Handle",
        tint = Color.Gray.copy(alpha = 0.6f),
        modifier = modifier
            .padding(vertical = 8.dp)
            .size(24.dp)
            .clip(ShapeDefaults.Large)
            .background(MaterialTheme.colorScheme.background)
    )
}

@Composable
@Preview
fun TrainingScreenPreview() {
    Workout__AndroidTheme {
        TrainingScreen()
    }
}

@Composable
@Preview
fun ExerciseItemPreview() {
    Workout__AndroidTheme {
        ExerciseItem(
            exercise = Exercise(0, trainingDayId = 0, name = "Exercise preview", reps = 10, sets = 10, setsDone = 2, type = ExerciseType.DYNAMIC, order = 0, rest = 10),
            draggableHandler = { DraggableHandler() },
            idx = 0
            )
    }
}
