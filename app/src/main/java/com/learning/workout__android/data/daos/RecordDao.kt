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
    fun create(record: RecordModel)

    @Update
    fun update(record: RecordModel)

    @Delete
    fun delete(record: RecordModel)
}