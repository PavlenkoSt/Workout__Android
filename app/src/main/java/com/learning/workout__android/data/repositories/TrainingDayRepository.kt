package com.learning.workout__android.data.repositories

import com.learning.workout__android.data.daos.TrainingDayDao
import com.learning.workout__android.data.models.TrainingDay
import kotlinx.coroutines.flow.Flow

class TrainingDayRepository (
    private val trainingDayDao: TrainingDayDao
) {
    fun getAll(): Flow<List<TrainingDay>> {
        return trainingDayDao.getAll()
    }

    fun getByDate(date: String): Flow<TrainingDay?> {
        return trainingDayDao.getByDate(date)
    }

    fun deleteByDate(date: String) {
        trainingDayDao.deleteByDate(date)
    }

    fun create(trainingDay: TrainingDay) {
        trainingDayDao.create(trainingDay)
    }

    fun update(trainingDay: TrainingDay) {
        trainingDayDao.update(trainingDay)
    }
}