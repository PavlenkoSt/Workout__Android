package com.stanislav_pav.repstation.ui.screens.trainingList

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.stanislav_pav.repstation.ui.components.NavBackIcon
import com.stanislav_pav.repstation.ui.theme.RepStationTheme
import com.stanislav_pav.repstation.viewModel.TrainingListFilterEnum
import com.stanislav_pav.repstation.viewModel.TrainingStatusSummary

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
            NavBackIcon(onBack = onBack)
        },
        actions = {
            TrainingListFilter(filter, onFilterChange, summary)
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TrainingListHeaderPreview() {
    RepStationTheme {
        TrainingListHeader(
            onBack = {},
            filter = TrainingListFilterEnum.All,
            onFilterChange = {},
            summary = TrainingStatusSummary()
        )
    }
}