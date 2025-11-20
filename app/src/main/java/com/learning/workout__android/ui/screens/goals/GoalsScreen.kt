package com.learning.workout__android.ui.screens.goals

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.viewModel.GoalsViewModel

@Composable
fun GoalsScreen(modifier: Modifier = Modifier) {
    val vm: GoalsViewModel =
        viewModel(factory = GoalsViewModel.provideFactory(LocalContext.current))
    val ui by vm.uiState.collectAsState()
    

    Text("GoalsScreen", modifier = modifier)
}