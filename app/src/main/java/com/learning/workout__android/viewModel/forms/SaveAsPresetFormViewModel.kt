package com.learning.workout__android.viewModel.forms

class SaveAsPresetFormViewModel :
    BaseFormViewModel<SaveAsPresetFormUiState, SaveAsPresetFormEvent>() {
    override fun createInitialState() = SaveAsPresetFormUiState()

    override fun applySeed(state: SaveAsPresetFormUiState, seedData: Any): SaveAsPresetFormUiState {
        return when (seedData) {
            // no seed handled to apply
            else -> state
        }
    }

    override fun onEvent(e: SaveAsPresetFormEvent) {
        when (e) {
            is SaveAsPresetFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = FormValidators::validateName
            )

            SaveAsPresetFormEvent.NameBlur -> blur(
                "name",
                FormValidators::validateName
            ) { it.copy(name = it.name.copy(touched = true)) }
        }
    }
}

// ---- UI State & Events ----

data class SaveAsPresetFormUiState(
    val name: Field = Field(),
) : BaseFormUiState {
    override val isValid: Boolean = name.error == null && name.value.isNotBlank()

    override fun validateAll(): BaseFormUiState =
        copy(name = name.copy(error = FormValidators.validateName(name.value), touched = true))

    override fun copyFieldError(
        fieldName: String,
        validator: (String) -> String?
    ): BaseFormUiState {
        return when (fieldName) {
            "name" -> copy(name = name.copy(error = validator(name.value)))
            else -> this
        }
    }
}

sealed interface SaveAsPresetFormEvent : BaseFormEvent {
    data class NameChanged(val v: String) : SaveAsPresetFormEvent
    data object NameBlur : SaveAsPresetFormEvent
}
