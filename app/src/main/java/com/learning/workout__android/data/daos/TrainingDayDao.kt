package com.learning.workout__android.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.TrainingDay
import com.learning.workout__android.data.models.TrainingDayWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDayDao {
    @Query("""
        SELECT * FROM training_days
    """)
    @Transaction
    fun getAll(): Flow<List<TrainingDayWithExercises>>

    @Query(value = """
        SELECT * FROM training_days WHERE date = :date
    """)
    @Transaction
    fun getByDate(date: String): Flow<TrainingDayWithExercises?>
    
    @Query("""
        SELECT * FROM exercises 
        WHERE trainingDayId = :trainingDayId 
        ORDER BY "order" ASC
    """)
    suspend fun getExercisesByTrainingDayId(trainingDayId: Int): List<Exercise>
    
    @Query("""
        UPDATE exercises 
        SET "order" = :order 
        WHERE id = :exerciseId
    """)
    suspend fun updateExerciseOrder(exerciseId: Int, order: Int)

    @Query("DELETE FROM training_days WHERE date = :date")
    @Transaction
    fun deleteByDate(date: String)

    @Insert
    suspend fun create(trainingDay: TrainingDay): Long

    @Update
    suspend fun update(trainingDay: TrainingDay)

    @Insert
    suspend fun insertExercise(exercise: Exercise): Long

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Query("DELETE FROM exercises WHERE trainingDayId = :trainingDayId")
    suspend fun deleteExercisesByTrainingDayId(trainingDayId: Int)
}