package com.learning.workout__android.ui.screens.presets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.data.models.ExerciseUnits
import com.learning.workout__android.ui.components.ModalHeader
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.forms.RecordFormEvent
import com.learning.workout__android.viewModel.forms.RecordFormSeed
import com.learning.workout__android.viewModel.forms.RecordFormViewModel

data class RecordFormResult(
    val name: String,
    val count: Number,
    val units: ExerciseUnits
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordForm(
    vm: RecordFormViewModel = viewModel(),
    onSubmit: (result: RecordFormResult) -> Unit,
    seed: RecordFormSeed,
    isEditing: Boolean
) {
    val focusManager = LocalFocusManager.current

    val ui by vm.ui.collectAsState()

    val countFocusRequester = remember { FocusRequester() }

    var dropdownExpanded by remember { mutableStateOf(false) }

    val units = ExerciseUnits.entries.toList()
    var selectedUnits by remember { mutableStateOf(ExerciseUnits.REPS) }

    LaunchedEffect(seed) {
        vm.seed(seed)
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.reset()
        }
    }

    Column(Modifier.padding(horizontal = 8.dp)) {
        ModalHeader(if (isEditing) "Update record" else "Add new record")

        OutlinedTextField(
            value = ui.name.value,
            onValueChange = { vm.onEvent(RecordFormEvent.NameChanged(it)) },
            isError = ui.name.touched && ui.name.error != null,
            supportingText = {
                if (ui.name.touched && ui.name.error != null) Text(ui.name.error!!)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Name") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    countFocusRequester.requestFocus()
                    vm.onEvent(RecordFormEvent.NameBlur)
                }
            )
        )

        OutlinedTextField(
            value = ui.count.value,
            onValueChange = { vm.onEvent(RecordFormEvent.CountChanged(it)) },
            isError = ui.count.touched && ui.count.error != null,
            supportingText = {
                if (ui.count.touched && ui.count.error != null) Text(ui.count.error!!)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(countFocusRequester),
            label = { Text(text = "Count") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Number
            ),
            keyboardActions = KeyboardActions {
                vm.onEvent(RecordFormEvent.CountBlur)
                focusManager.clearFocus()
            }
        )

        ExposedDropdownMenuBox(
            expanded = dropdownExpanded,
            onExpandedChange = { dropdownExpanded = !dropdownExpanded },
        ) {
            OutlinedTextField(
                value = selectedUnits.label,
                onValueChange = {},
                supportingText = {},
                readOnly = true,
                label = { Text("Units") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false }
            ) {
                units.forEach { unit ->
                    DropdownMenuItem(
                        text = { Text(unit.label) },
                        onClick = {
                            selectedUnits = unit
                            dropdownExpanded = false
                        }
                    )
                }
            }
        }

        Button(onClick = {
            val isValid = vm.submit()
            if (!isValid) return@Button
            onSubmit(
                RecordFormResult(
                    name = ui.name.value,
                    count = ui.count.value.toInt(),
                    units = selectedUnits
                )
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isEditing) "Update" else "Create")
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun RecordFormPreview() {
    Workout__AndroidTheme {
        RecordForm(
            onSubmit = {},
            seed = RecordFormSeed(),
            isEditing = false
        )
    }
}