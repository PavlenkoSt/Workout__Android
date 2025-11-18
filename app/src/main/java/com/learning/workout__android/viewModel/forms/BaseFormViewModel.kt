package com.learning.workout__android.viewModel.forms

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Generic form ViewModel that handles all state management logic.
 * T = your specific UI state type
 * E = your specific event type
 */
abstract class BaseFormViewModel<T : BaseFormUiState, E : BaseFormEvent> : ViewModel() {
    protected val _ui = MutableStateFlow(createInitialState())
    val ui: StateFlow<T> = _ui.asStateFlow()

    abstract fun createInitialState(): T
    abstract fun onEvent(e: E)
    abstract fun applySeed(state: T, seedData: Any): T

    fun submit(): Boolean {
        _ui.update { s ->
            s.validateAll() as T
        }
        return _ui.value.isValid
    }

    fun reset() {
        _ui.value = createInitialState()
    }

    fun seed(seedData: Any) {
        _ui.update { s ->
            applySeed(s, seedData)
        }
    }

    protected fun updateField(
        get: (T) -> Field,
        set: (T, Field) -> T,
        value: String,
        validator: (String) -> String?
    ) {
        _ui.update { s ->
            val current = get(s)
            val updated = current.copy(
                value = value,
                error = if (current.touched) validator(value) else null
            )
            set(s, updated)
        }
    }

    protected fun blur(
        fieldName: String,
        validator: (String) -> String?,
        touch: (T) -> T
    ) {
        _ui.update { s ->
            val t = touch(s)
            t.copyFieldError(fieldName, validator) as T
        }
    }
}

// Marker interfaces for type safety
interface BaseFormUiState {
    val isValid: Boolean
    fun validateAll(): BaseFormUiState
    fun copyFieldError(fieldName: String, validator: (String) -> String?): BaseFormUiState
}

interface BaseFormEvent

// Keep your existing Field and validators
data class Field(
    val value: String = "",
    val error: String? = null,
    val touched: Boolean = false,
    val isFocused: Boolean = false
)

// Common validators (reuse across forms)
object FormValidators {
    fun validateName(v: String): String? =
        if (v.trim().length < 2) "Name must be at least 2 chars" else null

    fun validatePositiveInt(v: String): String? {
        val trimmed = v.trim()
        if (trimmed.isEmpty()) return "Required"
        val number = trimmed.toIntOrNull() ?: return "Digits only"
        return if (number > 0) null else "Must be > 0"
    }

    fun validateNonNegativeInt(v: String): String? {
        val trimmed = v.trim()
        if (trimmed.isEmpty()) return "Required"
        val number = trimmed.toIntOrNull() ?: return "Digits only"
        return if (number >= 0) null else "Must be ≥ 0"
    }
}