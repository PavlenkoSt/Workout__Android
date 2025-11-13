package com.learning.workout__android.viewModel

import SharedSeed
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExerciseFormLadderViewModel : ViewModel() {
    private val _ui = MutableStateFlow(ExerciseLadderUiState())
    val ui: StateFlow<ExerciseLadderUiState> = _ui.asStateFlow()

    fun onEvent(e: ExerciseLadderFormEvent) {
        when (e) {
            is ExerciseLadderFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = ::validateName
            )

            is ExerciseLadderFormEvent.FromChanged -> updateField(
                get = { it.from },
                set = { s, f -> s.copy(from = f) },
                value = e.v.filter { ch -> ch.isDigit() }, // allow digits only
                validator = ::validatePositiveInt
            )

            is ExerciseLadderFormEvent.ToChanged -> updateField(
                get = { it.to },
                set = { s, f -> s.copy(to = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = ::validatePositiveInt
            )

            is ExerciseLadderFormEvent.StepChanged -> updateField(
                get = { it.step },
                set = { s, f -> s.copy(step = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = ::validatePositiveInt
            )

            is ExerciseLadderFormEvent.RestChanged -> updateField(
                get = { it.rest },
                set = { s, f -> s.copy(rest = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = ::validateNonNegativeInt
            )

            ExerciseLadderFormEvent.NameBlur -> blur(
                "name",
                ::validateName
            ) { it.copy(name = it.name.copy(touched = true)) }

            ExerciseLadderFormEvent.FromBlur -> blur(
                "from",
                ::validatePositiveInt
            ) { it.copy(from = it.from.copy(touched = true)) }

            ExerciseLadderFormEvent.ToBlur -> blur(
                "to",
                ::validatePositiveInt
            ) { it.copy(to = it.to.copy(touched = true)) }

            ExerciseLadderFormEvent.StepBlur -> blur(
                "step",
                ::validatePositiveInt
            ) { it.copy(step = it.step.copy(touched = true)) }

            ExerciseLadderFormEvent.RestBlur -> blur("rest", ::validateNonNegativeInt) {
                it.copy(
                    rest = it.rest.copy(touched = true)
                )
            }
        }
    }

    fun submit(): Boolean {
        // Validate all fields before submit
        _ui.update { s ->
            s.copy(
                name = s.name.copy(error = validateName(s.name.value), touched = true),
                from = s.from.copy(error = validatePositiveInt(s.from.value), touched = true),
                to = s.to.copy(error = validatePositiveInt(s.to.value), touched = true),
                step = s.step.copy(error = validatePositiveInt(s.step.value), touched = true),
                rest = s.rest.copy(error = validateNonNegativeInt(s.rest.value), touched = true)
            )
        }
        val current = _ui.value
        return current.isValid
    }

    fun seed(seed: SharedSeed) {
        _ui.update { s ->
            s.copy(
                name = s.name.copy(
                    value = seed.name,
                ),
                rest = s.rest.copy(
                    value = seed.rest.ifEmpty { "120" }
                ),
                step = s.step.copy(
                    value = "1"
                )
            )
        }
    }

    fun reset() {
        _ui.value = ExerciseLadderUiState()
    }

    private inline fun blur(
        fieldName: String,
        noinline validator: (String) -> String?,
        crossinline touch: (ExerciseLadderUiState) -> ExerciseLadderUiState
    ) {
        _ui.update { s ->
            val t = touch(s)
            t.copyFieldErrors(fieldName, validator)
        }
    }

    private fun ExerciseLadderUiState.copyFieldErrors(
        fieldName: String,
        validator: (String) -> String?
    ): ExerciseLadderUiState {
        return when (fieldName) {
            "name" -> copy(name = name.copy(error = validator(name.value)))
            "from" -> copy(from = from.copy(error = validator(from.value)))
            "to" -> copy(to = to.copy(error = validator(to.value)))
            "step" -> copy(step = step.copy(error = validator(step.value)))
            "rest" -> copy(rest = rest.copy(error = validator(rest.value)))
            else -> this
        }
    }

    private inline fun updateField(
        get: (ExerciseLadderUiState) -> Field,
        set: (ExerciseLadderUiState, Field) -> ExerciseLadderUiState,
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

data class ExerciseLadderUiState(
    val name: Field = Field(),
    val from: Field = Field(),
    val to: Field = Field(),
    val step: Field = Field(),
    val rest: Field = Field(),
    val isSubmitting: Boolean = false
) {
    val isValid: Boolean =
        name.error == null && from.error == null && to.error == null && step.error == null && rest.error == null &&
                name.value.isNotBlank() && from.value.isNotBlank() && to.value.isNotBlank() && step.value.isNotBlank() && rest.value.isNotBlank()
}

sealed interface ExerciseLadderFormEvent {
    data class NameChanged(val v: String) : ExerciseLadderFormEvent
    data class FromChanged(val v: String) : ExerciseLadderFormEvent
    data class ToChanged(val v: String) : ExerciseLadderFormEvent
    data class StepChanged(val v: String) : ExerciseLadderFormEvent
    data class RestChanged(val v: String) : ExerciseLadderFormEvent
    data object NameBlur : ExerciseLadderFormEvent
    data object FromBlur : ExerciseLadderFormEvent
    data object ToBlur : ExerciseLadderFormEvent
    data object StepBlur : ExerciseLadderFormEvent
    data object RestBlur : ExerciseLadderFormEvent
}
