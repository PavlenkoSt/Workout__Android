package com.learning.workout__android.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.learning.workout__android.data.models.RecordModel
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * from records")
    fun getAll(): Flow<List<RecordModel>>

    @Insert
    suspend fun create(record: RecordModel)

    @Update
    suspend fun update(record: RecordModel)

    @Delete
    suspend fun delete(record: RecordModel)
}