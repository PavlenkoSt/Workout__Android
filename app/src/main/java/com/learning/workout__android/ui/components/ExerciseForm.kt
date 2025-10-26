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
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.ui.theme.Workout__AndroidTheme

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

        Spacer(modifier = Modifier.height(8.dp))

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
    isEditing: Boolean
) {
    val focusManager = LocalFocusManager.current

    val repsFocusRequester = remember { FocusRequester() }
    val setsFocusRequester = remember { FocusRequester() }
    val restFocusRequester = remember { FocusRequester() }

    Column (modifier = Modifier.fillMaxWidth()) {
        TextField(value = "", onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Name")
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { repsFocusRequester.requestFocus() }
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row (modifier = Modifier.fillMaxWidth()) {
            TextField(value = "", onValueChange = {},
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
                    onNext = { setsFocusRequester.requestFocus() }
                ),
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(value = "", onValueChange = {},
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
                    onNext = { restFocusRequester.requestFocus() }
                ),
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