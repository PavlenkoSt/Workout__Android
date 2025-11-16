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

    @Insert
    suspend fun insertPreset(preset: Preset)

    @Delete
    suspend fun deletePreset(preset: Preset)

    @Update()
    suspend fun updatePreset(preset: Preset)

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
}