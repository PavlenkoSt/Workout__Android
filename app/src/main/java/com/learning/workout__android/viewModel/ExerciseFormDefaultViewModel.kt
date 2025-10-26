package com.learning.workout__android.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExerciseFormDefaultViewModel : ViewModel() {
    private val _ui = MutableStateFlow(ExerciseDefaultUiState())
    val ui: StateFlow<ExerciseDefaultUiState> = _ui.asStateFlow()

    fun onEvent(e: ExerciseDefaultFormEvent) {
        when (e) {
            is ExerciseDefaultFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = ::validateName
            )

            is ExerciseDefaultFormEvent.RepsChanged -> updateField(
                get = { it.reps },
                set = { s, f -> s.copy(reps = f) },
                value = e.v.filter { ch -> ch.isDigit() }, // allow digits only
                validator = ::validatePositiveInt
            )

            is ExerciseDefaultFormEvent.SetsChanged -> updateField(
                get = { it.sets },
                set = { s, f -> s.copy(sets = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = ::validatePositiveInt
            )

            is ExerciseDefaultFormEvent.RestChanged -> updateField(
                get = { it.rest },
                set = { s, f -> s.copy(rest = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = ::validateNonNegativeInt
            )

            ExerciseDefaultFormEvent.NameBlur -> blur("name", ::validateName) { it.copy(name = it.name.copy(touched = true)) }
            ExerciseDefaultFormEvent.RepsBlur -> blur("reps", ::validatePositiveInt) { it.copy(reps = it.reps.copy(touched = true)) }
            ExerciseDefaultFormEvent.SetsBlur -> blur("sets", ::validatePositiveInt) { it.copy(sets = it.sets.copy(touched = true)) }
            ExerciseDefaultFormEvent.RestBlur -> blur("rest", ::validateNonNegativeInt) { it.copy(rest = it.rest.copy(touched = true)) }
        }
    }

    fun submit(): Boolean {
        // Validate all fields before submit
        _ui.update { s ->
            s.copy(
                name = s.name.copy(error = validateName(s.name.value), touched = true),
                reps = s.reps.copy(error = validatePositiveInt(s.reps.value), touched = true),
                sets = s.sets.copy(error = validatePositiveInt(s.sets.value), touched = true),
                rest = s.rest.copy(error = validateNonNegativeInt(s.rest.value), touched = true)
            )
        }
        val current = _ui.value
        return current.isValid
    }

    fun reset() {
        _ui.value = ExerciseDefaultUiState()
    }

    private inline fun blur(
        fieldName: String,
        noinline validator: (String) -> String?,
        crossinline touch: (ExerciseDefaultUiState) -> ExerciseDefaultUiState
    ) {
        _ui.update { s ->
            val t = touch(s)
            t.copyFieldErrors(fieldName, validator)
        }
    }

    private fun ExerciseDefaultUiState.copyFieldErrors(
        fieldName: String,
        validator: (String) -> String?
    ): ExerciseDefaultUiState {
        return when (fieldName) {
            "name" -> copy(name = name.copy(error = validator(name.value)))
            "reps" -> copy(reps = reps.copy(error = validator(reps.value)))
            "sets" -> copy(sets = sets.copy(error = validator(sets.value)))
            "rest" -> copy(rest = rest.copy(error = validator(rest.value)))
            else -> this
        }
    }

    private inline fun updateField(
        get: (ExerciseDefaultUiState) -> Field,
        set: (ExerciseDefaultUiState, Field) -> ExerciseDefaultUiState,
        value: String,
        validator: (String) -> String?
    ) {
        _ui.update { s ->
            val current = get(s)
            val updated = current.copy(
                value = value,
                // live-validate if already touched; otherwise wait for blur/submit
                error = if (current.touched) validator(value) else null
            )
            set(s, updated)
        }
    }

    // --- Validators ---
    private fun validateName(v: String): String? =
        if (v.trim().length < 2) "Name must be at least 2 chars" else null

    private fun validatePositiveInt(v: String): String? {
        val trimmed = v.trim()
        if (trimmed.isEmpty()) return "Required"
        val number = trimmed.toIntOrNull() ?: return "Digits only"
        return if (number > 0) null else "Must be > 0"
    }

    private fun validateNonNegativeInt(v: String): String? {
        val trimmed = v.trim()
        if (trimmed.isEmpty()) return "Required"
        val number = trimmed.toIntOrNull() ?: return "Digits only"
        return if (number >= 0) null else "Must be ≥ 0"
    }
}

// ---- UI State & Events ----

data class Field(
    val value: String = "",
    val error: String? = null,
    val touched: Boolean = false,
    val isFocused: Boolean = false
)

data class ExerciseDefaultUiState(
    val name: Field = Field(),
    val reps: Field = Field(),
    val sets: Field = Field(),
    val rest: Field = Field(),
    val isSubmitting: Boolean = false
) {
    val isValid: Boolean =
        name.error == null && reps.error == null && sets.error == null && rest.error == null &&
                name.value.isNotBlank() && reps.value.isNotBlank() && sets.value.isNotBlank() && rest.value.isNotBlank()
}

sealed interface ExerciseDefaultFormEvent {
    data class NameChanged(val v: String) : ExerciseDefaultFormEvent
    data class RepsChanged(val v: String) : ExerciseDefaultFormEvent
    data class SetsChanged(val v: String) : ExerciseDefaultFormEvent
    data class RestChanged(val v: String) : ExerciseDefaultFormEvent
    data object NameBlur : ExerciseDefaultFormEvent
    data object RepsBlur : ExerciseDefaultFormEvent
    data object SetsBlur : ExerciseDefaultFormEvent
    data object RestBlur : ExerciseDefaultFormEvent
}