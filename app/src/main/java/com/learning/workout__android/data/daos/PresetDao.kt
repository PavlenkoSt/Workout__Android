package com.learning.workout__android.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.learning.workout__android.data.models.Preset
import com.learning.workout__android.data.models.PresetExercise
import com.learning.workout__android.data.models.PresetWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {
    @Query("SELECT * FROM presets ORDER BY `order` DESC")
    @Transaction
    fun getAllPresets(): Flow<List<PresetWithExercises>>

    @Query(
        """
        SELECT * FROM presets 
        WHERE id = :presetId
    """
    )
    suspend fun getPresetById(presetId: Long): Preset?

    @Query(
        """
        SELECT * FROM presets 
        WHERE id = :presetId
    """
    )
    @Transaction
    fun getPresetByIdWithExercises(presetId: Long): Flow<PresetWithExercises?>

    @Insert
    suspend fun insertPreset(preset: Preset): Long

    @Delete
    suspend fun deletePreset(preset: Preset)

    @Update()
    suspend fun updatePreset(preset: Preset)

    @Query("SELECT * from preset_exercises WHERE presetId = :presetId")
    suspend fun getExercisesByPresetId(presetId: Long): List<PresetExercise>

    @Query("SELECT * from preset_exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): PresetExercise?

    @Insert()
    suspend fun insertExercises(exercises: List<PresetExercise>)

    @Insert()
    suspend fun insertExercise(exercise: PresetExercise)

    @Update()
    suspend fun updateExercise(exercise: PresetExercise)

    @Delete()
    suspend fun deleteExercise(exercise: PresetExercise)

    @Query(
        """
        UPDATE presets 
        SET "order" = :order 
        WHERE id = :presetId
    """
    )
    suspend fun updatePresetOrder(presetId: Long, order: Long)

    @Query(
        """
        UPDATE preset_exercises 
        SET "order" = :order 
        WHERE id = :id
    """
    )
    suspend fun updateExerciseOrder(id: Long, order: Int)

    @Transaction
    suspend fun swapPresetOrder(fromPresetId: Long, toPresetId: Long) {
        // Fetch current state from database to avoid stale data
        val fromPreset = getPresetById(fromPresetId) ?: return
        val toPreset = getPresetById(toPresetId) ?: return

        // Only swap if orders are different
        if (fromPreset.order == toPreset.order) return

        // Perform atomic swap
        updatePresetOrder(fromPreset.id, toPreset.order)
        updatePresetOrder(toPreset.id, fromPreset.order)
    }

    @Transaction
    suspend fun swapExercisesOrder(fromExerciseId: Long, toExerciseId: Long) {
        // Fetch current state from database to avoid stale data
        val fromExercise = getExerciseById(fromExerciseId) ?: return
        val toExercise = getExerciseById(toExerciseId) ?: return

        // Only swap if orders are different
        if (fromExercise.order == toExercise.order) return

        // Perform atomic swap
        updateExerciseOrder(fromExercise.id, toExercise.order)
        updateExerciseOrder(toExercise.id, fromExercise.order)
    }

    @Transaction
    suspend fun createPresetWithExercises(presetWithExercises: PresetWithExercises) {
        val createdPresetId = insertPreset(presetWithExercises.preset)

        val exercisesWithPresetRelation =
            presetWithExercises.exercises.map { it.copy(presetId = createdPresetId) }

        insertExercises(exercisesWithPresetRelation)
    }
}