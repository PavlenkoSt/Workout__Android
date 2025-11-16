package com.learning.workout__android.ui.components.ExerciseForm

import ExerciseDefaultFormResult
import SharedSeed
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.TrainingExercise
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.ExerciseDefaultFormEvent
import com.learning.workout__android.viewModel.ExerciseFormDefaultViewModel


@Composable
fun ExerciseFormDefault(
    isStatic: Boolean? = false,
    exerciseToEdit: TrainingExercise?,
    onDefaultExerciseSubmit: (result: ExerciseDefaultFormResult) -> Unit,
    exerciseType: ExerciseType,
    vm: ExerciseFormDefaultViewModel = viewModel(),
    seed: SharedSeed,
    onSaveSeed: (seed: SharedSeed) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val ui by vm.ui.collectAsState()

    val repsFocusRequester = remember { FocusRequester() }
    val setsFocusRequester = remember { FocusRequester() }
    val restFocusRequester = remember { FocusRequester() }

    LaunchedEffect(seed, exerciseToEdit) {
        if (exerciseToEdit != null) {
            vm.seed(
                SharedSeed(
                    name = exerciseToEdit.name,
                    rest = exerciseToEdit.rest.toString(),
                    sets = exerciseToEdit.sets.toString(),
                    reps = exerciseToEdit.reps.toString()
                )
            )
            return@LaunchedEffect
        }
        vm.seed(seed)
    }

    DisposableEffect(Unit) {
        onDispose {
            onSaveSeed(
                SharedSeed(
                    name = ui.name.value,
                    rest = ui.rest.value,
                    sets = ui.sets.value,
                    reps = ui.reps.value
                )
            )
            vm.reset()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = ui.name.value,
            onValueChange = { vm.onEvent(ExerciseDefaultFormEvent.NameChanged(it)) },
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
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = ui.reps.value,
                onValueChange = { vm.onEvent(ExerciseDefaultFormEvent.RepsChanged(it)) },
                isError = ui.reps.touched && ui.reps.error != null,
                supportingText = {
                    if (ui.reps.touched && ui.reps.error != null) Text(ui.reps.error!!)
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(repsFocusRequester),
                label = {
                    Text(
                        text = if (isStatic == true) {
                            "Hold (sec)"
                        } else {
                            "Reps"
                        }
                    )
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
            OutlinedTextField(
                value = ui.sets.value,
                onValueChange = { vm.onEvent(ExerciseDefaultFormEvent.SetsChanged(it)) },
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
            OutlinedTextField(
                value = ui.rest.value,
                onValueChange = { vm.onEvent(ExerciseDefaultFormEvent.RestChanged(it)) },
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
        ExerciseFormSubmitBtn(onClick = {
            val isValid = vm.submit()
            if (!isValid) return@ExerciseFormSubmitBtn
            val result = ExerciseDefaultFormResult(
                ui.name.value.trim(),
                type = exerciseType,
                reps = ui.reps.value.trim().toInt(),
                sets = ui.sets.value.trim().toInt(),
                rest = ui.rest.value.trim().toInt(),
            )
            onDefaultExerciseSubmit(result)
        }, isEditing = exerciseToEdit != null)
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseFormDefaultPreview() {
    Workout__AndroidTheme {
        ExerciseFormDefault(
            onDefaultExerciseSubmit = {},
            seed = SharedSeed(),
            onSaveSeed = {},
            exerciseToEdit = null,
            exerciseType = ExerciseType.DYNAMIC
        )
    }
}