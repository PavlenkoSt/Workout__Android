package com.learning.workout__android.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.learning.workout__android.data.models.TrainingDay
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDayDao {
    @Query("SELECT * FROM trainingday")
    fun getAll(): Flow<List<TrainingDay>>

    @Query(value = "SELECT * FROM trainingday WHERE date = :date")
    fun getByDate(date: String): Flow<TrainingDay?>

    @Query("DELETE FROM trainingday WHERE date = :date")
    fun deleteByDate(date: String)

    @Insert
    fun create(trainingDay: TrainingDay)

    @Update
    fun update(trainingDay: TrainingDay)
}