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
        return trainingDayDao.getDayByDate(date)
    }

    fun deleteByDate(date: String) {
        trainingDayDao.deleteByDate(date)
    }

    suspend fun addExerciseToDate(date: String, exercise: Exercise) {
        val existingDay = trainingDayDao.getDayByDate(date).first()
        if (existingDay != null) {
            val trainingDayId = existingDay.trainingDay.id
            val existingExercises = trainingDayDao.getExercisesByTrainingDayId(trainingDayId)
            val maxOrder = existingExercises.maxOfOrNull { it.order } ?: -1
            trainingDayDao.insertExercise(exercise.copy(trainingDayId = trainingDayId, order = maxOrder + 1))
        } else {
            val createdId = trainingDayDao.create(TrainingDay(date = date))
            trainingDayDao.insertExercise(
                Exercise(
                    trainingDayId = createdId,
                    name = "Warmup",
                    type = ExerciseType.WARMUP,
                    reps = 1,
                    sets = 1,
                    setsDone = 0,
                    rest = 0,
                    order = 0
                )
            )
            trainingDayDao.insertExercise(exercise.copy(trainingDayId = createdId, order = 1))
        }
    }

    suspend fun addExercisesToDate(date: String, exercises: List<Exercise>) {
        val existingDay = trainingDayDao.getDayByDate(date).first()

        if (existingDay != null) {
            val existingExercises = trainingDayDao.getExercisesByTrainingDayId(existingDay.trainingDay.id)
            val maxOrder = existingExercises.maxOfOrNull { it.order } ?: -1
            exercises.forEachIndexed { index, exercise ->
                trainingDayDao.insertExercise(
                    exercise.copy(
                        trainingDayId = existingDay.trainingDay.id,
                        order = maxOrder + 1 + index)
                )
            }
        } else {
            val createdTrainingDayId = trainingDayDao.create(TrainingDay(date = date))

            trainingDayDao.insertExercise(Exercise(
                trainingDayId = createdTrainingDayId,
                name = "Warmup",
                type = ExerciseType.WARMUP,
                reps = 1,
                sets = 1,
                setsDone = 0,
                rest = 0,
                order = 0
            ))

            exercises.forEachIndexed { index, exercise ->
                trainingDayDao.insertExercise(
                    exercise.copy(trainingDayId = createdTrainingDayId, order = index + 1)
                )
            }
        }
    }

    suspend fun updateExercise(exercise: Exercise) {
        trainingDayDao.updateExercise(exercise)
    }

    suspend fun deleteExercise(exercise: Exercise) {
        trainingDayDao.deleteExercise(exercise)
    }

    suspend fun reorderExercises(trainingDayId: Long, fromIndex: Int, toIndex: Int) {
        val exercises = trainingDayDao.getExercisesByTrainingDayId(trainingDayId)
        if (fromIndex in exercises.indices && toIndex in 0..exercises.size) {
            val sortedExercises = exercises.sortedBy { it.order }.toMutableList()
            val item = sortedExercises.removeAt(fromIndex)
            sortedExercises.add(toIndex, item)
            
            // Update order for all affected exercises
            sortedExercises.forEachIndexed { index, exercise ->
                if (exercise.order != index) {
                    trainingDayDao.updateExerciseOrder(exercise.id, index)
                }
            }
        }
    }
}