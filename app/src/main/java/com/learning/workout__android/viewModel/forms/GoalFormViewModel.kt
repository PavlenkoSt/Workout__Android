package com.learning.workout__android.viewModel.forms

import com.learning.workout__android.data.models.GoalUnits

class GoalFormViewModel : BaseFormViewModel<GoalFormUiState, GoalFormEvent>() {
    override fun createInitialState() = GoalFormUiState()

    override fun applySeed(state: GoalFormUiState, seedData: Any): GoalFormUiState {
        return when (seedData) {
            is GoalFormSeed -> state.copy(
                name = state.name.copy(value = seedData.name),
                targetCount = state.targetCount.copy(value = seedData.targetCount)
            )

            else -> state
        }
    }

    override fun onEvent(e: GoalFormEvent) {
        when (e) {
            is GoalFormEvent.NameChanged -> updateField(
                get = { it.name },
                set = { s, f -> s.copy(name = f) },
                value = e.v,
                validator = FormValidators::validateName
            )

            is GoalFormEvent.TargetCountChanged -> updateField(
                get = { it.targetCount },
                set = { s, f -> s.copy(targetCount = f) },
                value = e.v.filter { ch -> ch.isDigit() }, // allow digits only
                validator = FormValidators::validatePositiveInt
            )

            GoalFormEvent.NameBlur -> blur(
                "name",
                FormValidators::validateName
            ) { it.copy(name = it.name.copy(touched = true)) }

            GoalFormEvent.TargetCountBlur -> blur(
                "targetCount",
                FormValidators::validatePositiveInt
            ) { it.copy(targetCount = it.targetCount.copy(touched = true)) }
        }
    }
}

// ---- UI State & Events ----

data class GoalFormUiState(
    val name: Field = Field(),
    val targetCount: Field = Field()
) : BaseFormUiState {
    override val isValid: Boolean =
        name.error == null
                && name.value.isNotBlank()
                && targetCount.error == null
                && targetCount.value.isNotBlank()
                && targetCount.value.toInt() > 0

    override fun validateAll(): BaseFormUiState =
        copy(
            name = name.copy(error = FormValidators.validateName(name.value), touched = true),
            targetCount = targetCount.copy(
                error = FormValidators.validatePositiveInt(targetCount.value),
                touched = true
            )
        )

    override fun copyFieldError(
        fieldName: String,
        validator: (String) -> String?
    ): BaseFormUiState {
        return when (fieldName) {
            "name" -> copy(name = name.copy(error = validator(name.value)))
            "targetCount" -> copy(targetCount = targetCount.copy(error = validator(targetCount.value)))
            else -> this
        }
    }
}

sealed interface GoalFormEvent : BaseFormEvent {
    data class NameChanged(val v: String) : GoalFormEvent
    data object NameBlur : GoalFormEvent

    data class TargetCountChanged(val v: String) : GoalFormEvent
    data object TargetCountBlur : GoalFormEvent
}

data class GoalFormSeed(
    val name: String = "",
    val targetCount: String = "",
    val units: GoalUnits = GoalUnits.REPS
)
