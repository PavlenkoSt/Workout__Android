package com.learning.workout__android.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.learning.workout__android.data.models.RecordModel
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * from records")
    fun getAll(): Flow<List<RecordModel>>

    @Query("SELECT * FROM records WHERE name = :name")
    suspend fun getRecordByName(name: String): RecordModel?

    @Insert
    suspend fun create(record: RecordModel)

    @Update
    suspend fun update(record: RecordModel)

    @Delete
    suspend fun delete(record: RecordModel)

    @Transaction
    suspend fun createRecordWithCheck(record: RecordModel): Boolean {
        val existingRecord = getRecordByName(record.name)

        if (existingRecord == null) {
            create(record)
            return true
        } else if (record.count >= existingRecord.count) {
            update(
                existingRecord.copy(
                    count = record.count,
                    units = record.units,
                    createdAt = record.createdAt
                )
            )
            return true
        }

      return false
    }
}