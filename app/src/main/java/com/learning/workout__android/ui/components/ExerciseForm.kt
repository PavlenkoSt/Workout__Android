package com.learning.workout__android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.ExerciseDefaultFormEvent
import com.learning.workout__android.viewModel.ExerciseFormDefaultViewModel
import com.learning.workout__android.viewModel.ExerciseLadderFormEvent
import com.learning.workout__android.viewModel.ExerciseFormLadderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseForm(
    onDefaultExerciseSubmit: (formResult: ExerciseDefaultFormResult) -> Unit,
    onLadderExerciseSubmit: (formResult: ExerciseLadderFormResult) -> Unit,
    onSimpleExerciseSubmit: (formResult: ExerciseSimpleFormResult) -> Unit,
    exerciseToEdit: Exercise?
) {
    val exerciseTypes =  listOf(
        ExerciseType.DYNAMIC,
        ExerciseType.STATIC,
        ExerciseType.LADDER,
        ExerciseType.HAND_BALANCE_SESSION,
        ExerciseType.FLEXIBILITY_SESSION,
        ExerciseType.WARMUP
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(exerciseTypes[0]) }

    Column (modifier = Modifier.padding(horizontal = 8.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                value = formatExerciseType(selectedType.label),
                onValueChange = {},
                supportingText = {},
                readOnly = true,
                label = { Text("Exercise Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                exerciseTypes.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(formatExerciseType(item.label)) },
                        onClick = {
                            selectedType = item
                            expanded = false
                        }
                    )
                }
            }
        }

        val isEditing = exerciseToEdit != null
        when(selectedType) {
            ExerciseType.DYNAMIC -> {
                ExerciseFormDefault(
                    exerciseToEdit = exerciseToEdit,
                    exerciseType = selectedType,
                    onDefaultExerciseSubmit = onDefaultExerciseSubmit
                )
            }
            ExerciseType.STATIC -> {
                ExerciseFormDefault(
                    isStatic = true,
                    exerciseToEdit = exerciseToEdit,
                    exerciseType = selectedType,
                    onDefaultExerciseSubmit = onDefaultExerciseSubmit
                )
            }
            ExerciseType.LADDER -> {
                ExerciseFormLadder(
                    onLadderExerciseSubmit = onLadderExerciseSubmit,
                    exerciseToEdit = exerciseToEdit
                )
            }
            else -> {
                SubmitBtn(
                    onClick = {
                        onSimpleExerciseSubmit(ExerciseSimpleFormResult(selectedType))
                    },
                    isEditing = exerciseToEdit != null
                )
            }
        }
    }
}

@Composable
fun ExerciseFormDefault(
    isStatic: Boolean? = false,
    exerciseToEdit: Exercise?,
    onDefaultExerciseSubmit: (result: ExerciseDefaultFormResult) -> Unit,
    exerciseType: ExerciseType,
    vm: ExerciseFormDefaultViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current

    val ui by vm.ui.collectAsState()

    val repsFocusRequester = remember { FocusRequester() }
    val setsFocusRequester = remember { FocusRequester() }
    val restFocusRequester = remember { FocusRequester() }

    DisposableEffect(Unit) {
        onDispose {
            vm.reset()
        }
    }

    Column (modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = ui.name.value,
            onValueChange = {vm.onEvent(ExerciseDefaultFormEvent.NameChanged(it))},
            modifier = Modifier.fillMaxWidth(),
            isError = ui.name.touched && ui.name.error != null,
            supportingText = {
                if (ui.name.touched && ui.name.error != null) Text(ui.name.error!!)
            },
            label = {
                Text(text = "Name")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    vm.onEvent(ExerciseDefaultFormEvent.NameBlur)
                    repsFocusRequester.requestFocus()
                }
            ),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row (modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = ui.reps.value,
                onValueChange = {vm.onEvent(ExerciseDefaultFormEvent.RepsChanged(it))},
                isError = ui.reps.touched && ui.reps.error != null,
                supportingText = {
                    if (ui.reps.touched && ui.reps.error != null) Text(ui.reps.error!!)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(repsFocusRequester),
                label = {
                    Text(text = if(isStatic == true) {"Hold (sec)"} else {"Reps"})
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        vm.onEvent(ExerciseDefaultFormEvent.RepsBlur)
                        setsFocusRequester.requestFocus()
                    }
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = ui.sets.value,
                onValueChange = {vm.onEvent(ExerciseDefaultFormEvent.SetsChanged(it))},
                isError = ui.sets.touched && ui.sets.error != null,
                supportingText = {
                    if (ui.sets.touched && ui.sets.error != null) Text(ui.sets.error!!)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(setsFocusRequester),
                label = {
                    Text(text = "Sets")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        vm.onEvent(ExerciseDefaultFormEvent.SetsBlur)
                        restFocusRequester.requestFocus()
                    }
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = ui.rest.value,
                onValueChange = {vm.onEvent(ExerciseDefaultFormEvent.RestChanged(it))},
                isError = ui.rest.touched && ui.rest.error != null,
                supportingText = {
                    if (ui.rest.touched && ui.rest.error != null) Text(ui.rest.error!!)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(restFocusRequester),
                label = {
                    Text(text = "Rest")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        vm.onEvent(ExerciseDefaultFormEvent.RestBlur)
                        focusManager.clearFocus()
                    }
                )
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        SubmitBtn(onClick = {
            val isValid = vm.submit()
            if(!isValid) return@SubmitBtn
            val result = ExerciseDefaultFormResult(
                ui.name.value,
                type = exerciseType,
                reps = ui.reps.value.toInt(),
                sets = ui.sets.value.toInt(),
                rest = ui.rest.value.toInt(),
            )
            onDefaultExerciseSubmit(result)
        }, isEditing = exerciseToEdit != null)
    }
}


@Composable
fun ExerciseFormLadder(
    onLadderExerciseSubmit: (result: ExerciseLadderFormResult) -> Unit,
    exerciseToEdit: Exercise?,
    vm: ExerciseFormLadderViewModel = viewModel()
) {
    val focusManager = LocalFocusManager.current

    val ui by vm.ui.collectAsState()

    val fromFocusRequester = remember { FocusRequester() }
    val toFocusRequester = remember { FocusRequester() }
    val stepFocusRequester = remember { FocusRequester() }
    val restFocusRequester = remember { FocusRequester() }

    DisposableEffect(Unit) {
        onDispose {
            vm.reset()
        }
    }

    Column (modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = ui.name.value,
            onValueChange = {vm.onEvent(ExerciseLadderFormEvent.NameChanged(it))},
            modifier = Modifier.fillMaxWidth(),
            isError = ui.name.touched && ui.name.error != null,
            supportingText = {
                if (ui.name.touched && ui.name.error != null) Text(ui.name.error!!)
            },
            label = {
                Text(text = "Name")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    vm.onEvent(ExerciseLadderFormEvent.NameBlur)
                    fromFocusRequester.requestFocus()
                }
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row (modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = ui.from.value,
                onValueChange = {vm.onEvent(ExerciseLadderFormEvent.FromChanged(it))},
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(fromFocusRequester),
                isError = ui.from.touched && ui.from.error != null,
                supportingText = {
                    if (ui.from.touched && ui.from.error != null) Text(ui.from.error!!)
                },
                label = {
                    Text(text = "From")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        vm.onEvent(ExerciseLadderFormEvent.FromBlur)
                        toFocusRequester.requestFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = ui.to.value,
                onValueChange = {vm.onEvent(ExerciseLadderFormEvent.ToChanged(it))},
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(toFocusRequester),
                isError = ui.to.touched && ui.to.error != null,
                supportingText = {
                    if (ui.to.touched && ui.to.error != null) Text(ui.to.error!!)
                },
                label = {
                    Text(text = "To")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        vm.onEvent(ExerciseLadderFormEvent.ToBlur)
                        stepFocusRequester.requestFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = ui.step.value,
                onValueChange = {vm.onEvent(ExerciseLadderFormEvent.StepChanged(it))},
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(stepFocusRequester),
                isError = ui.step.touched && ui.step.error != null,
                supportingText = {
                    if (ui.step.touched && ui.step.error != null) Text(ui.step.error!!)
                },
                label = {
                    Text(text = "Step")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        vm.onEvent(ExerciseLadderFormEvent.StepBlur)
                        restFocusRequester.requestFocus()
                    }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = ui.rest.value,
                onValueChange = {vm.onEvent(ExerciseLadderFormEvent.RestChanged(it))},
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(restFocusRequester),
                isError = ui.rest.touched && ui.rest.error != null,
                supportingText = {
                    if (ui.rest.touched && ui.rest.error != null) Text(ui.rest.error!!)
                },
                label = {
                    Text(text = "Rest")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        vm.onEvent(ExerciseLadderFormEvent.RestBlur)
                        focusManager.clearFocus()
                    }
                )
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        SubmitBtn(onClick = {
            val isValid = vm.submit()
            if(!isValid) return@SubmitBtn
            val result = ExerciseLadderFormResult(
                name = ui.name.value,
                type = ExerciseType.LADDER,
                from = ui.from.value.toInt(),
                to = ui.to.value.toInt(),
                step = ui.step.value.toInt(),
                rest = ui.rest.value.toInt()
            )
            onLadderExerciseSubmit(result)
        }, isEditing = exerciseToEdit != null)
    }
}

@Composable
fun SubmitBtn(
    onClick: () -> Unit,
    isEditing: Boolean
) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(text =  if(isEditing) {"Update"} else {"Add +"})
    }
}

private fun formatExerciseType(type: String): String {
    return type
        .lowercase()
        .replace("_", " ")
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
}

data class ExerciseDefaultFormResult (
    val name: String,
    val type: ExerciseType,
    val reps: Number,
    val sets: Number,
    val rest: Number
)

data class ExerciseSimpleFormResult (
    val type: ExerciseType
)

data class ExerciseLadderFormResult (
    val name: String,
    val type: ExerciseType,
    val from: Number,
    val to: Number,
    val step: Number,
    val rest: Number
)

@Preview
@Composable
fun ExerciseFormPreview () {
    Workout__AndroidTheme {
        ExerciseForm(
            onLadderExerciseSubmit = {},
            onSimpleExerciseSubmit = {},
            onDefaultExerciseSubmit = {},
            exerciseToEdit = null
        )
    }
}