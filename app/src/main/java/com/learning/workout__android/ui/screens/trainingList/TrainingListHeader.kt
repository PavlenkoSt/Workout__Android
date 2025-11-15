package com.learning.workout__android.ui.screens.trainingList

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.TrainingListFilterEnum
import com.learning.workout__android.viewModel.TrainingStatusSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingListHeader(
    onBack: () -> Unit,
    filter: TrainingListFilterEnum,
    onFilterChange: (TrainingListFilterEnum) -> Unit,
    summary: TrainingStatusSummary
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            Text("Training days")
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            TrainingListFilter(filter, onFilterChange, summary)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TrainingListHeaderPreview() {
    Workout__AndroidTheme {
        TrainingListHeader(
            onBack = {},
            filter = TrainingListFilterEnum.All,
            onFilterChange = {},
            summary = TrainingStatusSummary()
        )
    }
}