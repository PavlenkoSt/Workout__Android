package com.learning.workout__android.ui.screens.goals

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
import com.learning.workout__android.utils.formatExerciseType
import com.learning.workout__android.viewModel.forms.GoalFormEvent
import com.learning.workout__android.viewModel.forms.GoalFormSeed
import com.learning.workout__android.viewModel.forms.GoalFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalForm(
    vm: GoalFormViewModel = viewModel(),
    onSubmit: (result: GoalFormResult) -> Unit,
    seed: GoalFormSeed,
    isEditing: Boolean
) {
    val focusManager = LocalFocusManager.current

    val ui by vm.ui.collectAsState()

    val countFocusRequester = remember { FocusRequester() }

    val units = ExerciseUnits.entries.toTypedArray()

    var expanded by remember { mutableStateOf(false) }
    var selectedUnits by remember { mutableStateOf(ExerciseUnits.REPS) }

    LaunchedEffect(seed) {
        vm.seed(seed)
        selectedUnits = seed.units
    }

    DisposableEffect(Unit) {
        onDispose {
            vm.reset()
        }
    }

    Column(Modifier.padding(horizontal = 8.dp)) {
        ModalHeader(if (isEditing) "Update goal" else "Add new goal")

        OutlinedTextField(
            value = ui.name.value,
            onValueChange = { vm.onEvent(GoalFormEvent.NameChanged(it)) },
            isError = ui.name.touched && ui.name.error != null,
            supportingText = {
                if (ui.name.touched && ui.name.error != null) Text(ui.name.error!!)
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Exercise") },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions {
                countFocusRequester.requestFocus()
                vm.onEvent(GoalFormEvent.NameBlur)
            }
        )

        OutlinedTextField(
            value = ui.targetCount.value,
            onValueChange = {vm.onEvent(GoalFormEvent.TargetCountChanged(it))},
            isError = ui.targetCount.touched && ui.targetCount.error != null,
            supportingText = {
                if (ui.targetCount.touched && ui.targetCount.error != null) Text(ui.targetCount.error!!)
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
                vm.onEvent(GoalFormEvent.TargetCountBlur)
                focusManager.clearFocus()
            }
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = formatExerciseType(selectedUnits.label),
                onValueChange = {},
                supportingText = {},
                readOnly = true,
                label = { Text("Units") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                units.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(formatExerciseType(item.label)) },
                        onClick = {
                            selectedUnits = item
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(onClick = {
            val isValid = vm.submit()
            if (!isValid) return@Button
            onSubmit(
                GoalFormResult(
                    name = ui.name.value.trim(),
                    targetCount = ui.targetCount.value.toInt(),
                    units = selectedUnits
                )
            )
        }, modifier = Modifier.fillMaxWidth()) {
            Text(if (isEditing) "Update" else "Create")
        }
    }
}

data class GoalFormResult(
    val name: String,
    val targetCount: Int,
    val units: ExerciseUnits
)

@Composable
@Preview(showBackground = true)
private fun GoalFormPreview() {
    Workout__AndroidTheme {
        GoalForm(
            onSubmit = {},
            seed = GoalFormSeed(),
            isEditing = false
        )
    }
}