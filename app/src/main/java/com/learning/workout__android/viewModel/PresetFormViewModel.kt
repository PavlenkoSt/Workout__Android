package com.learning.workout__android.viewModel

import SharedSeed
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PresetFormViewModel : ViewModel() {
    private val _ui = MutableStateFlow(PresetFormUiState())
    val ui: StateFlow<PresetFormUiState> = _ui.asStateFlow()

    fun onEvent(e: PresetFormEvent) {
        when (e) {
            is PresetFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = ::validateName
            )

            PresetFormEvent.NameBlur -> blur(
                "name",
                ::validateName
            ) { it.copy(name = it.name.copy(touched = true)) }
        }
    }

    fun submit(): Boolean {
        // Validate all fields before submit
        _ui.update { s ->
            s.copy(
                name = s.name.copy(error = validateName(s.name.value), touched = true),
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
            )
        }
    }

    fun reset() {
        _ui.value = PresetFormUiState()
    }

    private inline fun blur(
        fieldName: String,
        noinline validator: (String) -> String?,
        crossinline touch: (PresetFormUiState) -> PresetFormUiState
    ) {
        _ui.update { s ->
            val t = touch(s)
            t.copyFieldErrors(fieldName, validator)
        }
    }

    private fun PresetFormUiState.copyFieldErrors(
        fieldName: String,
        validator: (String) -> String?
    ): PresetFormUiState {
        return when (fieldName) {
            "name" -> copy(name = name.copy(error = validator(name.value)))
            else -> this
        }
    }

    private inline fun updateField(
        get: (PresetFormUiState) -> Field,
        set: (PresetFormUiState, Field) -> PresetFormUiState,
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
}

// ---- UI State & Events ----

data class PresetFormUiState(
    val name: Field = Field(),
) {
    val isValid: Boolean = name.error == null && name.value.isNotBlank()
}

sealed interface PresetFormEvent {
    data class NameChanged(val v: String) : PresetFormEvent
    data object NameBlur : PresetFormEvent
}
