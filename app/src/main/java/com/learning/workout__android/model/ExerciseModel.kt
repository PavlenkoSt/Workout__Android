package com.learning.workout__android.model

data class ExerciseModel(
    val id: Int,
    var exercise: String,
    var reps: Int,
    var sets: Int,
    var setsDone: Int,
    var rest: Int,
    var type: ExerciseType
)

enum class ExerciseType {
    DYNAMIC,
    STATIC,
    LADDER,
    WARMUP,
    FLEXIBILITY_SESSION,
    HANDBALANCE_SESSION,
}