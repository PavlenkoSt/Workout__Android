package com.learning.workout__android.data.daos

import androidx.room.Dao
import androidx.room.Delete
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
    @Query("SELECT * FROM training_days")
    @Transaction
    fun getAllTrainingDays(): Flow<List<TrainingDayWithExercises>>

    @Query(
        value = """
        SELECT * FROM training_days WHERE date = :date
    """
    )
    fun getDayByDate(date: String): Flow<TrainingDayWithExercises?>

    @Query(
        """
        SELECT * FROM exercises 
        WHERE trainingDayId = :trainingDayId 
        ORDER BY "order" ASC
    """
    )
    suspend fun getExercisesByTrainingDayId(trainingDayId: Long): List<Exercise>

    @Query(
        """
        UPDATE exercises 
        SET "order" = :order 
        WHERE id = :exerciseId
    """
    )
    suspend fun updateExerciseOrder(exerciseId: Long, order: Int)

    @Query(
        """
        SELECT * FROM exercises 
        WHERE id = :exerciseId
    """
    )
    suspend fun getExerciseById(exerciseId: Long): Exercise?

    @Query("DELETE FROM training_days WHERE date = :date")
    fun deleteByDate(date: String)

    @Insert
    suspend fun create(trainingDay: TrainingDay): Long

    @Update
    suspend fun update(trainingDay: TrainingDay)

    @Insert
    suspend fun insertExercise(exercise: Exercise): Long

    @Update
    suspend fun updateExercise(exercise: Exercise)

    @Delete
    suspend fun deleteExercise(exercise: Exercise)

    @Transaction
    suspend fun swapExerciseOrder(fromExerciseId: Long, toExerciseId: Long) {
        // Fetch current state from database to avoid stale data
        val fromExercise = getExerciseById(fromExerciseId) ?: return
        val toExercise = getExerciseById(toExerciseId) ?: return
        
        // Only swap if orders are different
        if (fromExercise.order == toExercise.order) return
        
        // Perform atomic swap
        updateExerciseOrder(fromExercise.id, toExercise.order)
        updateExerciseOrder(toExercise.id, fromExercise.order)
    }

    @Query("DELETE FROM exercises WHERE trainingDayId = :trainingDayId")
    fun deleteExercisesByTrainingDayId(trainingDayId: Long)

    @Query("SELECT id FROM training_days WHERE date = :date")
    suspend fun getTrainingDayIdByDate(date: String): Long?

    @Transaction
    suspend fun deleteTrainingDayWithExercises(date: String) {
        val trainingDayId = getTrainingDayIdByDate(date)
        trainingDayId?.let {
            deleteExercisesByTrainingDayId(it)
            deleteByDate(date)
        }
    }
}