package com.learning.workout__android.ui.screens.presets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.ui.components.SearchTextField
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsHeader(
    search: String,
    onSearchChange: (String) -> Unit,
    onAddPresetClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
        title = {
            SearchTextField(value = search, onValueChange = onSearchChange)
        },
        actions = {
            Row {
                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(
                    onClick = onAddPresetClick, modifier = Modifier.height(48.dp),
                    shape = ShapeDefaults.Small,
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(MaterialTheme.colorScheme.onBackground),
                        width = 1.dp
                    )
                ) {
                    Text("+ Add", color = MaterialTheme.colorScheme.onBackground)
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    )
}


@Composable
@Preview(showBackground = true)
private fun PresetsHeaderPreview() {
    Workout__AndroidTheme {
        PresetsHeader(
            search = "",
            onSearchChange = {},
            onAddPresetClick = {}
        )
    }
}