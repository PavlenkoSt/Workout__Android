package com.learning.workout__android.data.repositories

import com.learning.workout__android.data.daos.RecordDao
import com.learning.workout__android.data.models.RecordModel
import kotlinx.coroutines.flow.Flow

class RecordsRepository(
    private val recordDao: RecordDao
) {
    fun getAllRecords(): Flow<List<RecordModel>> {
        return recordDao.getAll()
    }

    fun createRecord(record: RecordModel) {
        recordDao.create(record)
    }

    fun updateRecord(record: RecordModel) {
        recordDao.update(record)
    }

    fun deleteRecord(record: RecordModel) {
        recordDao.delete(record)
    }
}