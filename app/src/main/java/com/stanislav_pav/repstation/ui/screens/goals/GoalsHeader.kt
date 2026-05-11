package com.stanislav_pav.repstation.ui.screens.records

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stanislav_pav.repstation.ui.components.ProIndicator
import com.stanislav_pav.repstation.ui.screens.goals.GoalsFilter
import com.stanislav_pav.repstation.ui.theme.RepStationTheme
import com.stanislav_pav.repstation.viewModel.GoalsFilterEnum
import com.stanislav_pav.repstation.viewModel.GoalsStatusSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsHeader(
    filter: GoalsFilterEnum,
    onFilterChange: (filter: GoalsFilterEnum) -> Unit,
    summary: GoalsStatusSummary
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset((-2).dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                GoalsFilter(filter = filter, onFilterChange = onFilterChange, summary = summary)
            }
        },
        actions = {
            ProIndicator()
            Spacer(modifier = Modifier.width(12.dp))
        }
    )
}

@Composable
@Preview(showBackground = true)
private fun GoalsHeaderPreview() {
    RepStationTheme {
        GoalsHeader(
            filter = GoalsFilterEnum.All,
            onFilterChange = {},
            summary = GoalsStatusSummary()
        )
    }
}