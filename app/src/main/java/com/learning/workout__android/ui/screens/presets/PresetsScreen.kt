package com.learning.workout__android.ui.screens.presets

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.viewModel.PresetsViewModel

@Composable
fun PresetsScreen(modifier: Modifier = Modifier) {
    val vm: PresetsViewModel = viewModel(
        factory = PresetsViewModel.provideFactory(LocalContext.current)
    )
    val ui by vm.uiState.collectAsState()

    Text("PresetsScreen", modifier = modifier)
}