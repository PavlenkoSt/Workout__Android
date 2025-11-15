package com.learning.workout__android.ui.components.ExerciseForm

import ExerciseLadderFormResult
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
import com.learning.workout__android.data.models.TrainingExercise
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.viewModel.ExerciseFormLadderViewModel
import com.learning.workout__android.viewModel.ExerciseLadderFormEvent


@Composable
fun ExerciseFormLadder(
    onLadderExerciseSubmit: (result: ExerciseLadderFormResult) -> Unit,
    exerciseToEdit: TrainingExercise?,
    vm: ExerciseFormLadderViewModel = viewModel(),
    seed: SharedSeed,
    onSaveSeed: (seed: SharedSeed) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val ui by vm.ui.collectAsState()

    val fromFocusRequester = remember { FocusRequester() }
    val toFocusRequester = remember { FocusRequester() }
    val stepFocusRequester = remember { FocusRequester() }
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
                    rest = ui.rest.value
                )
            )
            vm.reset()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = ui.name.value,
            onValueChange = { vm.onEvent(ExerciseLadderFormEvent.NameChanged(it)) },
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
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = ui.from.value,
                onValueChange = { vm.onEvent(ExerciseLadderFormEvent.FromChanged(it)) },
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
            OutlinedTextField(
                value = ui.to.value,
                onValueChange = { vm.onEvent(ExerciseLadderFormEvent.ToChanged(it)) },
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
            OutlinedTextField(
                value = ui.step.value,
                onValueChange = { vm.onEvent(ExerciseLadderFormEvent.StepChanged(it)) },
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
            OutlinedTextField(
                value = ui.rest.value,
                onValueChange = { vm.onEvent(ExerciseLadderFormEvent.RestChanged(it)) },
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
        ExerciseFormSubmitBtn(onClick = {
            val isValid = vm.submit()
            if (!isValid) return@ExerciseFormSubmitBtn
            val result = ExerciseLadderFormResult(
                name = ui.name.value,
                from = ui.from.value.toInt(),
                to = ui.to.value.toInt(),
                step = ui.step.value.toInt(),
                rest = ui.rest.value.toInt()
            )
            onLadderExerciseSubmit(result)
        }, isEditing = exerciseToEdit != null)
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseFormLadderPreview() {
    Workout__AndroidTheme {
        ExerciseFormLadder(
            onLadderExerciseSubmit = {},
            onSaveSeed = {},
            seed = SharedSeed(),
            exerciseToEdit = null
        )
    }
}