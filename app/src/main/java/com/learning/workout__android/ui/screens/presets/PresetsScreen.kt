package com.learning.workout__android.ui.screens.presets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.LoadState
import com.learning.workout__android.viewModel.PresetsViewModel
import com.learning.workout__android.viewModel.forms.PresetFormSeed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PresetsScreen(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()

    val vm: PresetsViewModel = viewModel(
        factory = PresetsViewModel.provideFactory(LocalContext.current)
    )
    val ui by vm.uiState.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PresetsHeader(
                search = vm.search.collectAsState().value,
                onSearchChange = { vm.onSearch(it) }
            )

            when (val state = ui.allPresets) {
                is LoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }

                is LoadState.Success -> {
                    if (state.data.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(
                                text = "No presets found",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {
                        PresetsList(
                            presets = state.data,
                            reorderPresets = { from, to -> vm.reorderPresets(from, to) },
                            onSwipeToEdit = { preset ->
                                vm.setPresetToEdit(preset)
                                showBottomSheet = true
                            },
                            onSwipeToDelete = { preset ->
                                vm.deletePreset(preset.preset)
                            }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showBottomSheet = true },
            shape = ShapeDefaults.ExtraLarge,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(all = 16.dp)
                .height(50.dp)
                .width(50.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }

        if (showBottomSheet) {
            fun onHide() {
                coroutineScope.launch {
                    sheetState.hide()
                    showBottomSheet = false
                    vm.setPresetToEdit(null)
                }
            }

            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    vm.setPresetToEdit(null)
                },
                sheetState = sheetState
            ) {
                PresetForm(
                    onSubmit = {
                        if (ui.presetToEdit != null) {
                            vm.updatePreset(it)
                        } else {
                            vm.createPreset(it)
                        }
                        onHide()
                    },
                    seed = PresetFormSeed(name = ui.presetToEdit?.preset?.name ?: ""),
                    isEditing = ui.presetToEdit != null
                )
            }
        }
    }
}

@Composable
@Preview
private fun PresetsScreenPreview() {
    Workout__AndroidTheme {
        PresetsScreen()
    }
}