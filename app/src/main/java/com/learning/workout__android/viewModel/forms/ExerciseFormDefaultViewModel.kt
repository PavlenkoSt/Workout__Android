package com.learning.workout__android.viewModel.forms

import ExerciseSharedSeed

class ExerciseFormDefaultViewModel :
    BaseFormViewModel<ExerciseDefaultUiState, ExerciseDefaultFormEvent>() {
    override fun createInitialState() = ExerciseDefaultUiState()

    override fun applySeed(state: ExerciseDefaultUiState, seedData: Any): ExerciseDefaultUiState {
        return when (seedData) {
            is ExerciseSharedSeed -> state.copy(
                name = state.name.copy(value = seedData.name),
                rest = state.rest.copy(value = seedData.rest.ifEmpty { "120" }),
                sets = state.sets.copy(value = seedData.sets),
                reps = state.reps.copy(value = seedData.reps)
            )

            else -> state
        }
    }

    override fun onEvent(e: ExerciseDefaultFormEvent) {
        when (e) {
            is ExerciseDefaultFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = FormValidators::validateName
            )

            is ExerciseDefaultFormEvent.RepsChanged -> updateField(
                get = { it.reps },
                set = { s, f -> s.copy(reps = f) },
                value = e.v.filter { ch -> ch.isDigit() }, // allow digits only
                validator = FormValidators::validatePositiveInt
            )

            is ExerciseDefaultFormEvent.SetsChanged -> updateField(
                get = { it.sets },
                set = { s, f -> s.copy(sets = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = FormValidators::validatePositiveInt
            )

            is ExerciseDefaultFormEvent.RestChanged -> updateField(
                get = { it.rest },
                set = { s, f -> s.copy(rest = f) },
                value = e.v.filter { ch -> ch.isDigit() },
                validator = FormValidators::validateNonNegativeInt
            )

            ExerciseDefaultFormEvent.NameBlur -> blur(
                "name",
                FormValidators::validateName
            ) { it.copy(name = it.name.copy(touched = true)) }

            ExerciseDefaultFormEvent.RepsBlur -> blur(
                "reps",
                FormValidators::validatePositiveInt
            ) { it.copy(reps = it.reps.copy(touched = true)) }

            ExerciseDefaultFormEvent.SetsBlur -> blur(
                "sets",
                FormValidators::validatePositiveInt
            ) { it.copy(sets = it.sets.copy(touched = true)) }

            ExerciseDefaultFormEvent.RestBlur -> blur(
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

data class ExerciseDefaultUiState(
    val name: Field = Field(),
    val reps: Field = Field(),
    val sets: Field = Field(),
    val rest: Field = Field(),
    val isSubmitting: Boolean = false
) : BaseFormUiState {
    override val isValid: Boolean =
        name.error == null && reps.error == null && sets.error == null &&
                rest.error == null &&
                name.value.isNotBlank() && reps.value.isNotBlank() &&
                sets.value.isNotBlank() && rest.value.isNotBlank()

    override fun validateAll(): BaseFormUiState = copy(
        name = name.copy(error = FormValidators.validateName(name.value), touched = true),
        reps = reps.copy(error = FormValidators.validatePositiveInt(reps.value), touched = true),
        sets = sets.copy(error = FormValidators.validatePositiveInt(sets.value), touched = true),
        rest = rest.copy(error = FormValidators.validatePositiveInt(rest.value), touched = true)
    )

    override fun copyFieldError(
        fieldName: String,
        validator: (String) -> String?
    ): BaseFormUiState {
        return when (fieldName) {
            "name" -> copy(name = name.copy(error = validator(name.value)))
            "reps" -> copy(reps = reps.copy(error = validator(reps.value)))
            "sets" -> copy(sets = sets.copy(error = validator(sets.value)))
            "rest" -> copy(rest = rest.copy(error = validator(rest.value)))
            else -> this
        }
    }
}

sealed interface ExerciseDefaultFormEvent : BaseFormEvent {
    data class NameChanged(val v: String) : ExerciseDefaultFormEvent
    data class RepsChanged(val v: String) : ExerciseDefaultFormEvent
    data class SetsChanged(val v: String) : ExerciseDefaultFormEvent
    data class RestChanged(val v: String) : ExerciseDefaultFormEvent
    data object NameBlur : ExerciseDefaultFormEvent
    data object RepsBlur : ExerciseDefaultFormEvent
    data object SetsBlur : ExerciseDefaultFormEvent
    data object RestBlur : ExerciseDefaultFormEvent
}