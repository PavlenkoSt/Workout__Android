package com.learning.workout__android.viewModel.forms

class RecordFormViewModel : BaseFormViewModel<RecordFormUiState, RecordFormEvent>() {
    override fun createInitialState() = RecordFormUiState()

    override fun applySeed(state: RecordFormUiState, seedData: Any): RecordFormUiState {
        return when (seedData) {
            is RecordFormSeed -> state.copy(
                name = state.name.copy(value = seedData.name),
                count = state.count.copy(value = seedData.count)
            )

            else -> state
        }
    }

    override fun onEvent(e: RecordFormEvent) {
        when (e) {
            is RecordFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = FormValidators::validateName
            )

            RecordFormEvent.NameBlur -> blur(
                "name",
                FormValidators::validateName
            ) { it.copy(name = it.name.copy(touched = true)) }

            is RecordFormEvent.CountChanged -> updateField(
                get = { it.count },
                set = { s, f -> s.copy(count = f) },
                value = e.v,
                validator = FormValidators::validatePositiveInt
            )

            RecordFormEvent.CountBlur -> blur(
                "count",
                FormValidators::validatePositiveInt
            ) { it.copy(count = it.count.copy(touched = true)) }
        }
    }
}

// ---- UI State & Events ----

data class RecordFormUiState(
    val name: Field = Field(),
    val count: Field = Field()
) : BaseFormUiState {
    override val isValid: Boolean =
        name.error == null
                && name.value.isNotBlank()
                && count.error == null
                && count.value.isNotBlank()
                && count.value.toInt() > 0

    override fun validateAll(): BaseFormUiState =
        copy(
            name = name.copy(error = FormValidators.validateName(name.value), touched = true),
            count = count.copy(
                error = FormValidators.validatePositiveInt(count.value),
                touched = true
            )
        )

    override fun copyFieldError(
        fieldName: String,
        validator: (String) -> String?
    ): BaseFormUiState {
        return when (fieldName) {
            "name" -> copy(name = name.copy(error = validator(name.value)))
            "count" -> copy(count = count.copy(error = validator(count.value)))
            else -> this
        }
    }

}

sealed interface RecordFormEvent : BaseFormEvent {
    data class NameChanged(val v: String) : RecordFormEvent
    data object NameBlur : RecordFormEvent

    data class CountChanged(val v: String) : RecordFormEvent
    data object CountBlur : RecordFormEvent
}

data class RecordFormSeed(
    val name: String = "",
    val count: String = "",
)
