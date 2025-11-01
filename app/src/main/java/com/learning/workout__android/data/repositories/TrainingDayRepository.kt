package com.learning.workout__android.data.repositories

import com.learning.workout__android.data.daos.TrainingDayDao
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.ExerciseType
import com.learning.workout__android.data.models.TrainingDay
import com.learning.workout__android.data.models.TrainingDayWithExercises
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class TrainingDayRepository (
    private val trainingDayDao: TrainingDayDao
) {
    fun getAll(): Flow<List<TrainingDayWithExercises>> {
        return trainingDayDao.getAll()
    }

    fun getByDate(date: String): Flow<TrainingDayWithExercises?> {
        return trainingDayDao.getByDate(date)
    }

    fun deleteByDate(date: String) {
        trainingDayDao.deleteByDate(date)
    }

    suspend fun update(trainingDay: TrainingDayWithExercises) {
        trainingDayDao.update(trainingDay.trainingDay)
        trainingDayDao.deleteExercisesByTrainingDayId(trainingDay.trainingDay.id)
        trainingDay.exercises.forEach { exercise ->
            trainingDayDao.insertExercise(exercise.copy(trainingDayId = trainingDay.trainingDay.id))
        }
    }

    suspend fun addExerciseToDate(date: String, exercise: Exercise) {
        val existingDay = trainingDayDao.getByDate(date).first()
        val trainingDayId = if (existingDay != null) {
            existingDay.trainingDay.id
        } else {
            val createdTrainingDayId = trainingDayDao.create(TrainingDay(date = date)).toInt()

            trainingDayDao.insertExercise(Exercise(
                trainingDayId = createdTrainingDayId,
                name = "Warmup",
                type = ExerciseType.WARMUP,
                reps = 1,
                sets = 1,
                setsDone = 0,
                rest = 0
            ))

           createdTrainingDayId
        }

        trainingDayDao.insertExercise(exercise.copy(trainingDayId = trainingDayId))
    }

    suspend fun addExercisesToDate(date: String, exercises: List<Exercise>) {
        val existingDay = trainingDayDao.getByDate(date).first()
        val trainingDayId = if (existingDay != null) {
            existingDay.trainingDay.id
        } else {
            val createdTrainingDayId = trainingDayDao.create(TrainingDay(date = date)).toInt()

            trainingDayDao.insertExercise(Exercise(
                trainingDayId = createdTrainingDayId,
                name = "Warmup",
                type = ExerciseType.WARMUP,
                reps = 1,
                sets = 1,
                setsDone = 0,
                rest = 0
            ))

           createdTrainingDayId
        }

        exercises.forEach { exercise ->
            trainingDayDao.insertExercise(exercise.copy(trainingDayId = trainingDayId))
        }
    }
}