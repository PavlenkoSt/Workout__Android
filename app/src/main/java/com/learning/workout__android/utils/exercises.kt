package com.learning.workout__android.utils

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