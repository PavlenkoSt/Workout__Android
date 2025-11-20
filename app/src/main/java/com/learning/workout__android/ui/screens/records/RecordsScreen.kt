package com.learning.workout__android.ui.screens.records

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.viewModel.RecordsViewModel

@Composable
fun RecordsScreen(modifier: Modifier = Modifier) {
    val vm: RecordsViewModel =
        viewModel(factory = RecordsViewModel.provideFactory(LocalContext.current))
    val ui by vm.uiState.collectAsState()

    Text("RecordsScreen", modifier = modifier)
}