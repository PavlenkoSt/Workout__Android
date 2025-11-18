package com.learning.workout__android.viewModel.forms

class PresetFormViewModel : BaseFormViewModel<PresetFormUiState, PresetFormEvent>() {
    override fun createInitialState() = PresetFormUiState()

    override fun applySeed(state: PresetFormUiState, seedData: Any): PresetFormUiState {
        return when (seedData) {
            is PresetFormSeed -> state.copy(
                name = state.name.copy(value = seedData.name),
            )

            else -> state
        }
    }

    override fun onEvent(e: PresetFormEvent) {
        when (e) {
            is PresetFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = FormValidators::validateName
            )

            PresetFormEvent.NameBlur -> blur(
                "name",
                FormValidators::validateName
            ) { it.copy(name = it.name.copy(touched = true)) }
        }
    }
}

// ---- UI State & Events ----

data class PresetFormUiState(
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

sealed interface PresetFormEvent : BaseFormEvent {
    data class NameChanged(val v: String) : PresetFormEvent
    data object NameBlur : PresetFormEvent
}

data class PresetFormSeed(
    val name: String = ""
)
