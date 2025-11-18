package com.learning.workout__android.viewModel.forms

import ExerciseSharedSeed

class ExerciseFormLadderViewModel :
    BaseFormViewModel<ExerciseLadderUiState, ExerciseLadderFormEvent>() {
    override fun createInitialState() = ExerciseLadderUiState()

    override fun applySeed(state: ExerciseLadderUiState, seedData: Any): ExerciseLadderUiState {
        return when (seedData) {
            is ExerciseSharedSeed -> state.copy(
                name = state.name.copy(value = seedData.name),
                rest = state.rest.copy(value = seedData.rest.ifEmpty { "120" }),
                step = state.step.copy(value = "1")
            )

            else -> state
        }
    }

    override fun onEvent(e: ExerciseLadderFormEvent) {
        when (e) {
            is ExerciseLadderFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = FormValidators::validateName
            )

            is ExerciseLadderFormEvent.FromChanged -> updateField(
                get = { it.from },
                set = { s, f -> s.copy(from = f) },
                value = e.v.filter { ch -> ch.isDigit() }, // allow digits only
                validator = FormValidators::validatePositiveInt
            )

            is ExerciseLadderFormEvent.ToChanged -> updateField(
                get = { it.to },
                set = { s, f -> s.copy(to = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = FormValidators::validatePositiveInt
            )

            is ExerciseLadderFormEvent.StepChanged -> updateField(
                get = { it.step },
                set = { s, f -> s.copy(step = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = FormValidators::validatePositiveInt
            )

            is ExerciseLadderFormEvent.RestChanged -> updateField(
                get = { it.rest },
                set = { s, f -> s.copy(rest = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = FormValidators::validateNonNegativeInt
            )

            ExerciseLadderFormEvent.NameBlur -> blur(
                "name",
                FormValidators::validateName
            ) { it.copy(name = it.name.copy(touched = true)) }

            ExerciseLadderFormEvent.FromBlur -> blur(
                "from",
                FormValidators::validatePositiveInt
            ) { it.copy(from = it.from.copy(touched = true)) }

            ExerciseLadderFormEvent.ToBlur -> blur(
                "to",
                FormValidators::validatePositiveInt
            ) { it.copy(to = it.to.copy(touched = true)) }

            ExerciseLadderFormEvent.StepBlur -> blur(
                "step",
                FormValidators::validatePositiveInt
            ) { it.copy(step = it.step.copy(touched = true)) }

            ExerciseLadderFormEvent.RestBlur -> blur(
                "rest",
                FormValidators::validateNonNegativeInt
            ) {
                it.copy(
                    rest = it.rest.copy(touched = true)
                )
            }
        }
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
) : BaseFormUiState {
    override val isValid: Boolean =
        name.error == null && from.error == null && to.error == null &&
                step.error == null && rest.error == null &&
                name.value.isNotBlank() && from.value.isNotBlank() &&
                to.value.isNotBlank() && step.value.isNotBlank() && rest.value.isNotBlank()

    override fun validateAll(): BaseFormUiState = copy(
        name = name.copy(error = FormValidators.validateName(name.value), touched = true),
        from = from.copy(error = FormValidators.validatePositiveInt(from.value), touched = true),
        to = to.copy(error = FormValidators.validatePositiveInt(to.value), touched = true),
        step = step.copy(error = FormValidators.validatePositiveInt(step.value), touched = true),
        rest = rest.copy(error = FormValidators.validateNonNegativeInt(rest.value), touched = true)
    )

    override fun copyFieldError(
        fieldName: String,
        validator: (String) -> String?
    ): BaseFormUiState {
        return when (fieldName) {
            "name" -> copy(name = name.copy(error = validator(name.value)))
            "from" -> copy(from = from.copy(error = validator(from.value)))
            "to" -> copy(to = to.copy(error = validator(to.value)))
            "step" -> copy(step = step.copy(error = validator(step.value)))
            "rest" -> copy(rest = rest.copy(error = validator(rest.value)))
            else -> this
        }
    }
}

sealed interface ExerciseLadderFormEvent : BaseFormEvent {
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
