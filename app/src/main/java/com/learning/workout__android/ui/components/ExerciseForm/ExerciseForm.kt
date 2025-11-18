package com.learning.workout__android.ui.components.ExerciseForm

import ExerciseDefaultFormResult
import ExerciseLadderFormResult
import ExerciseSharedSeed
import ExerciseSimpleFormResult
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
import com.learning.workout__android.ui.components.ModalHeader
import com.learning.workout__android.ui.theme.Workout__AndroidTheme
import com.learning.workout__android.utils.formatExerciseType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseForm(
    onDefaultExerciseSubmit: (formResult: ExerciseDefaultFormResult) -> Unit,
    onLadderExerciseSubmit: (formResult: ExerciseLadderFormResult) -> Unit,
    onSimpleExerciseSubmit: (formResult: ExerciseSimpleFormResult) -> Unit,
    exerciseEditingFields: ExerciseEditingFields?
) {
    val exerciseTypes = if (exerciseEditingFields != null) listOf(
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

    var sharedSeed by remember { mutableStateOf(ExerciseSharedSeed()) }

    fun onSaveSeed(seed: ExerciseSharedSeed) {
        sharedSeed = seed
    }

    LaunchedEffect(exerciseEditingFields?.type) {
        if (exerciseEditingFields != null) {
            selectedType = exerciseEditingFields.type
        }
    }

    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        ModalHeader(if (exerciseEditingFields == null) "Add exercise" else "Edit exercise")

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
                    exerciseEditingFields = exerciseEditingFields,
                    exerciseType = selectedType,
                    onDefaultExerciseSubmit = onDefaultExerciseSubmit,
                    seed = sharedSeed,
                    onSaveSeed = { onSaveSeed(it) }
                )
            }

            ExerciseType.STATIC -> {
                ExerciseFormDefault(
                    isStatic = true,
                    exerciseEditingFields = exerciseEditingFields,
                    exerciseType = selectedType,
                    onDefaultExerciseSubmit = onDefaultExerciseSubmit,
                    seed = sharedSeed,
                    onSaveSeed = { onSaveSeed(it) }
                )
            }

            ExerciseType.LADDER -> {
                ExerciseFormLadder(
                    onLadderExerciseSubmit = onLadderExerciseSubmit,
                    exerciseEditingFields = exerciseEditingFields,
                    seed = sharedSeed,
                    onSaveSeed = { onSaveSeed(it) }
                )
            }

            else -> {
                LaunchedEffect(Unit) {
                    onSaveSeed(ExerciseSharedSeed())
                }

                ExerciseFormSubmitBtn(
                    onClick = {
                        onSimpleExerciseSubmit(ExerciseSimpleFormResult(selectedType))
                    },
                    isEditing = exerciseEditingFields != null,
                )
            }
        }
    }
}

data class ExerciseEditingFields(
    val name: String,
    val reps: Int,
    val sets: Int,
    val rest: Int,
    val type: ExerciseType
)

@Preview(showBackground = true)
@Composable
private fun ExerciseFormPreview() {
    Workout__AndroidTheme {
        ExerciseForm(
            onLadderExerciseSubmit = {},
            onSimpleExerciseSubmit = {},
            onDefaultExerciseSubmit = {},
            exerciseEditingFields = null
        )
    }
}