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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseForm(
    onDefaultExerciseSubmit: () -> Unit,
    onLadderExerciseSubmit: () -> Unit,
    onSimpleExerciseSubmit: () -> Unit,
    exerciseToEdit: Exercise?
) {
    val exerciseTypes = listOf(
        ExerciseType.DYNAMIC.label,
        ExerciseType.STATIC.label,
        ExerciseType.LADDER.label,
        ExerciseType.HAND_BALANCE_SESSION.label,
        ExerciseType.FLEXIBILITY_SESSION.label,
        ExerciseType.WARMUP.label
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(exerciseTypes[0]) }

    Column (modifier = Modifier.padding(horizontal = 8.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            TextField(
                value = formatExerciseType(selectedText),
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
                        text = { Text(formatExerciseType(item)) },
                        onClick = {
                            selectedText = item
                            expanded = false
                        }
                    )
                }
            }
        }

        val isEditing = exerciseToEdit != null
        when(selectedText) {
            ExerciseType.DYNAMIC.label -> { ExerciseFormDefault(isEditing = isEditing) }
            ExerciseType.STATIC.label -> { ExerciseFormDefault(isStatic = true, isEditing = isEditing) }
            ExerciseType.LADDER.label -> { ExerciseFormLadder(isEditing = isEditing) }
            else -> { SubmitBtn(onClick = {}, isEditing = isEditing) }
        }
    }
}

@Composable
fun ExerciseFormDefault(
    isStatic: Boolean? = false,
    isEditing: Boolean, // TODO change to exercise to edit and propagate to form as initial values
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
            // TODO add exercise to db
        }, isEditing = isEditing)
    }
}


@Composable
fun ExerciseFormLadder(
    isEditing: Boolean
) {
    val focusManager = LocalFocusManager.current

    val fromFocusRequester = remember { FocusRequester() }
    val toFocusRequester = remember { FocusRequester() }
    val stepFocusRequester = remember { FocusRequester() }
    val restFocusRequester = remember { FocusRequester() }

    Column (modifier = Modifier.fillMaxWidth()) {
        TextField(value = "", onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Name")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions = KeyboardActions(
                onNext = { fromFocusRequester.requestFocus() }
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row (modifier = Modifier.fillMaxWidth()) {
            TextField(value = "", onValueChange = {},
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(fromFocusRequester)
                ,
                label = {
                    Text(text = "From")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { toFocusRequester.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(value = "", onValueChange = {},
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(toFocusRequester)
                ,
                label = {
                    Text(text = "To")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { stepFocusRequester.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(value = "", onValueChange = {},
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(stepFocusRequester),
                label = {
                    Text(text = "Step")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { restFocusRequester.requestFocus() }
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(value = "", onValueChange = {},
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
                    onDone = { focusManager.clearFocus() }
                )
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        SubmitBtn(onClick = {}, isEditing = isEditing)
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