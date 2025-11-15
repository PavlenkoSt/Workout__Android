package com.learning.workout__android.utils

import com.learning.workout__android.data.models.TrainingExercise
import com.learning.workout__android.data.models.ExerciseType

fun formatExerciseType(type: String): String {
    return type
        .lowercase()
        .replace("_", " ")
        .split(" ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { word ->
            word.replaceFirstChar { it.uppercase() }
        }
}

fun formatExerciseName(exercise: TrainingExercise): String {
    return when (exercise.type) {
        ExerciseType.FLEXIBILITY_SESSION -> "Flexibility session"
        ExerciseType.HAND_BALANCE_SESSION -> "Hand balance session"
        ExerciseType.WARMUP -> "Warmup"
        else -> exercise.name
    }
}