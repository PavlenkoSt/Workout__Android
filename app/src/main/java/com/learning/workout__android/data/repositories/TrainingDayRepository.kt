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
        if (existingDay != null) {
            val trainingDayId = existingDay.trainingDay.id
            // Get max order to append new exercise at the end
            val existingExercises = trainingDayDao.getExercisesByTrainingDayId(trainingDayId)
            val maxOrder = existingExercises.maxOfOrNull { it.order } ?: -1
            trainingDayDao.insertExercise(exercise.copy(trainingDayId = trainingDayId, order = maxOrder + 1))
        } else {
            val createdTrainingDayId = trainingDayDao.create(TrainingDay(date = date)).toInt()

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

            trainingDayDao.insertExercise(exercise.copy(trainingDayId = createdTrainingDayId, order = 1))
            createdTrainingDayId
        }
    }

    suspend fun addExercisesToDate(date: String, exercises: List<Exercise>) {
        val existingDay = trainingDayDao.getByDate(date).first()

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
            val createdTrainingDayId = trainingDayDao.create(TrainingDay(date = date)).toInt()

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

    suspend fun reorderExercises(trainingDayId: Int, fromIndex: Int, toIndex: Int) {
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