package com.stanislav_pav.repstation.utils

import com.stanislav_pav.repstation.data.models.ExerciseType

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

fun formatExerciseName(name: String, type: ExerciseType): String {
    return when (type) {
        ExerciseType.FLEXIBILITY_SESSION -> "Flexibility session"
        ExerciseType.HAND_BALANCE_SESSION -> "Hand balance session"
        ExerciseType.WARMUP -> "Warmup"
        else -> name
    }
}
