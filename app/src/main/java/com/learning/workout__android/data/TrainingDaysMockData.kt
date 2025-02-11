package com.learning.workout__android.data

import com.learning.workout__android.model.ExerciseModel
import com.learning.workout__android.model.ExerciseType
import com.learning.workout__android.model.TrainingModel
import java.time.LocalDate

val TrainingDaysMockData = listOf(
    TrainingModel(
        date = LocalDate.now().toString(),
        exercises = listOf(
            ExerciseModel(
                id = 0,
                exercise = "Pull ups",
                reps = 10,
                sets = 5,
                setsDone = 1,
                rest = 60,
                type = ExerciseType.DYNAMIC
            ),
            ExerciseModel(
                id = 1,
                exercise = "Push ups",
                reps = 100,
                sets = 52,
                setsDone = 11,
                rest = 60,
                type = ExerciseType.DYNAMIC
            ),
            ExerciseModel(
                id = 2,
                exercise = "Plank",
                reps = 120,
                sets = 2,
                setsDone = 1,
                rest = 60,
                type = ExerciseType.STATIC
            )
        )
    )
)