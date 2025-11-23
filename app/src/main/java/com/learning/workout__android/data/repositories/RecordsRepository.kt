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

    suspend fun createRecordWithCheck(record: RecordModel): Boolean {
        return recordDao.createRecordWithCheck(record)
    }

    suspend fun createRecord(record: RecordModel) {
        recordDao.create(record)
    }

    suspend fun updateRecord(record: RecordModel) {
        recordDao.update(record)
    }

    suspend fun deleteRecord(record: RecordModel) {
        recordDao.delete(record)
    }
}