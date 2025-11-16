package com.learning.workout__android.ui.screens.training

import ExerciseDefaultFormResult
import ExerciseLadderFormResult
import ExerciseSimpleFormResult
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.learning.workout__android.ui.components.ExerciseForm.ExerciseEditingFields
import com.learning.workout__android.ui.components.ExerciseForm.ExerciseForm
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseModal(
    onDismiss: () -> Unit,
    onDefaultExerciseSubmit: (formResult: ExerciseDefaultFormResult) -> Unit,
    onLadderExerciseSubmit: (formResult: ExerciseLadderFormResult) -> Unit,
    onSimpleExerciseSubmit: (formResult: ExerciseSimpleFormResult) -> Unit,
    exerciseEditingFields: ExerciseEditingFields?
) {
    val coroutineScope = rememberCoroutineScope()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun onHide() {
        coroutineScope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState
    ) {
        ExerciseForm(
            onLadderExerciseSubmit = {
                onLadderExerciseSubmit(it)
                onHide()
            },
            onDefaultExerciseSubmit = {
                onDefaultExerciseSubmit(it)
                onHide()
            },
            onSimpleExerciseSubmit = {
                onSimpleExerciseSubmit(it)
                onHide()
            },
            exerciseEditingFields = exerciseEditingFields
        )
    }
}