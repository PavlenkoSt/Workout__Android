package com.learning.workout__android.ui.screens.training

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.ui.components.ModalHeader
import com.learning.workout__android.viewModel.SaveAsPresetFormEvent
import com.learning.workout__android.viewModel.SaveAsPresetFormViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveAsPresetModal(
    onDismiss: () -> Unit,
    onSubmit: (name: String) -> Unit,
    vm: SaveAsPresetFormViewModel = viewModel(),
) {
    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val ui by vm.ui.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            vm.reset()
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            ModalHeader("Save as preset")

            OutlinedTextField(
                value = ui.name.value,
                onValueChange = { vm.onEvent(SaveAsPresetFormEvent.NameChanged(it)) },
                isError = ui.name.touched && ui.name.error != null,
                supportingText = {
                    if (ui.name.touched && ui.name.error != null) Text(ui.name.error!!)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                label = {
                    Text(text = "Name")
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        vm.onEvent(SaveAsPresetFormEvent.NameBlur)
                    }
                ),
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                val isValid = vm.submit()
                if (!isValid) return@Button
                onSubmit(ui.name.value.trim())

                coroutineScope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            }, modifier = Modifier.fillMaxWidth()) {
                Text("Save")
            }
        }
    }
}