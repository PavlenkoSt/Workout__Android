package com.learning.workout__android.ui.screens.presets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.ui.components.ModalHeader
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.PresetFormEvent
import com.learning.workout__android.viewModel.PresetFormViewModel

@Composable
fun PresetForm(
    vm: PresetFormViewModel = viewModel(),
    onSubmit: (name: String) -> Unit
) {
    val ui by vm.ui.collectAsState()

    Column(Modifier.padding(horizontal = 8.dp)) {
        ModalHeader("Add new preset")

        OutlinedTextField(
            value = ui.name.value,
            onValueChange = { vm.onEvent(PresetFormEvent.NameChanged(it)) },
            isError = ui.name.touched && ui.name.error != null,
            supportingText = {
                if (ui.name.touched && ui.name.error != null) Text(ui.name.error!!)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions {
                vm.onEvent(PresetFormEvent.NameBlur)
            }
        )
        Button(onClick = {
            val isValid = vm.submit()
            if (!isValid) return@Button

            onSubmit(ui.name.value)
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Create")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PresetFormPreview() {
    Workout__AndroidTheme {
        PresetForm(
            onSubmit = {}
        )
    }
}