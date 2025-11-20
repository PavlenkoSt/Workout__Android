package com.learning.workout__android.ui.screens.presets

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.ui.components.SearchTextField
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsHeader(
    search: String,
    onSearchChange: (String) -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            SearchTextField(
                value = search,
                onValueChange = onSearchChange,
                modifier = Modifier.offset(x = (-8).dp)
            )
        }
    )
}


@Composable
@Preview(showBackground = true)
private fun PresetsHeaderPreview() {
    Workout__AndroidTheme {
        PresetsHeader(
            search = "",
            onSearchChange = {}
        )
    }
}