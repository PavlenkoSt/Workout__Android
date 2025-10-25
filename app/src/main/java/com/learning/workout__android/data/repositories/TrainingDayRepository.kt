package com.learning.workout__android.data.repositories

import com.learning.workout__android.data.daos.TrainingDayDao
import com.learning.workout__android.data.models.TrainingDayWithExercises
import kotlinx.coroutines.flow.Flow

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

    suspend fun create(trainingDay: TrainingDayWithExercises) {
        val trainingDayId = trainingDayDao.create(trainingDay.trainingDay).toInt()
        trainingDay.exercises.forEach { exercise ->
            trainingDayDao.insertExercise(exercise.copy(trainingDayId = trainingDayId))
        }
    }

    suspend fun update(trainingDay: TrainingDayWithExercises) {
        trainingDayDao.update(trainingDay.trainingDay)
        trainingDayDao.deleteExercisesByTrainingDayId(trainingDay.trainingDay.id)
        trainingDay.exercises.forEach { exercise ->
            trainingDayDao.insertExercise(exercise.copy(trainingDayId = trainingDay.trainingDay.id))
        }
    }
}