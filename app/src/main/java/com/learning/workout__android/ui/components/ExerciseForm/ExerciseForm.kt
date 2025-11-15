package com.learning.workout__android.ui.components.ExerciseForm

import ExerciseDefaultFormResult
import ExerciseLadderFormResult
import ExerciseSimpleFormResult
import SharedSeed
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.TrainingExercise
import com.learning.workout__android.ui.components.ModalHeader
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatExerciseType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseForm(
    onDefaultExerciseSubmit: (formResult: ExerciseDefaultFormResult) -> Unit,
    onLadderExerciseSubmit: (formResult: ExerciseLadderFormResult) -> Unit,
    onSimpleExerciseSubmit: (formResult: ExerciseSimpleFormResult) -> Unit,
    exerciseToEdit: TrainingExercise?
) {
    val exerciseTypes = if (exerciseToEdit != null) listOf(
        ExerciseType.DYNAMIC,
        ExerciseType.STATIC,
        ExerciseType.HAND_BALANCE_SESSION,
        ExerciseType.FLEXIBILITY_SESSION,
        ExerciseType.WARMUP
    ) else listOf(
        ExerciseType.DYNAMIC,
        ExerciseType.STATIC,
        ExerciseType.LADDER,
        ExerciseType.HAND_BALANCE_SESSION,
        ExerciseType.FLEXIBILITY_SESSION,
        ExerciseType.WARMUP
    )
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf(exerciseTypes[0]) }

    var sharedSeed by remember { mutableStateOf(SharedSeed()) }

    fun onSaveSeed(seed: SharedSeed) {
        sharedSeed = seed
    }

    LaunchedEffect(exerciseToEdit?.type) {
        if (exerciseToEdit != null) {
            selectedType = exerciseToEdit.type
        }
    }

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        ModalHeader(if (exerciseToEdit == null) "Add exercise" else "Edit exercise")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = formatExerciseType(selectedType.label),
                onValueChange = {},
                supportingText = {},
                readOnly = true,
                label = { Text("Exercise Type") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
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

        when (selectedType) {
            ExerciseType.DYNAMIC -> {
                ExerciseFormDefault(
                    exerciseToEdit = exerciseToEdit,
                    exerciseType = selectedType,
                    onDefaultExerciseSubmit = onDefaultExerciseSubmit,
                    seed = sharedSeed,
                    onSaveSeed = { onSaveSeed(it) }
                )
            }

            ExerciseType.STATIC -> {
                ExerciseFormDefault(
                    isStatic = true,
                    exerciseToEdit = exerciseToEdit,
                    exerciseType = selectedType,
                    onDefaultExerciseSubmit = onDefaultExerciseSubmit,
                    seed = sharedSeed,
                    onSaveSeed = { onSaveSeed(it) }
                )
            }

            ExerciseType.LADDER -> {
                ExerciseFormLadder(
                    onLadderExerciseSubmit = onLadderExerciseSubmit,
                    exerciseToEdit = exerciseToEdit,
                    seed = sharedSeed,
                    onSaveSeed = { onSaveSeed(it) }
                )
            }

            else -> {
                LaunchedEffect(Unit) {
                    onSaveSeed(SharedSeed())
                }

                ExerciseFormSubmitBtn(
                    onClick = {
                        onSimpleExerciseSubmit(ExerciseSimpleFormResult(selectedType))
                    },
                    isEditing = exerciseToEdit != null,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ExerciseFormPreview() {
    Workout__AndroidTheme {
        ExerciseForm(
            onLadderExerciseSubmit = {},
            onSimpleExerciseSubmit = {},
            onDefaultExerciseSubmit = {},
            exerciseToEdit = null
        )
    }
}