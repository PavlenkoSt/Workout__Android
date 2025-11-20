package com.learning.workout__android.ui.screens.records

import androidx.compose.foundation.layout.Box
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
import com.learning.workout__android.ui.screens.presets.RecordForm
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.LoadState
import com.learning.workout__android.viewModel.RecordsViewModel
import com.learning.workout__android.viewModel.forms.RecordFormSeed
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsScreen(modifier: Modifier = Modifier) {
    val vm: RecordsViewModel =
        viewModel(factory = RecordsViewModel.provideFactory(LocalContext.current))
    val ui by vm.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        RecordsHeader()

        when (val state = ui.records) {
            is LoadState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is LoadState.Success -> {
                if (state.data.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(text = "No records yet", modifier = Modifier.align(Alignment.Center))
                    }
                } else {
                    RecordsList(records = state.data)
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
                    vm.setRecordToEdit(null)
                }
            }

            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    vm.setRecordToEdit(null)
                },
                sheetState = sheetState
            ) {
                RecordForm(
                    onSubmit = {
                        // TODO action here
//                        if (ui.recordToEdit != null) {
//                            vm.updateRecord(it)
//                        } else {
//                            vm.createRecord(it)
//                        }
                        onHide()
                    },
                    seed = RecordFormSeed(
                        name = ui.recordToEdit?.name ?: "",
                        count = if (ui.recordToEdit != null) ui.recordToEdit?.count.toString() else "",
                    ),
                    isEditing = ui.recordToEdit != null
                )
            }
        }
    }
}

@Composable
@Preview
private fun RecordsScreenPreview() {
    Workout__AndroidTheme {
        RecordsScreen()
    }
}