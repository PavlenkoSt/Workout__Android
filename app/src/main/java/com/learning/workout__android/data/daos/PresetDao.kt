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
    @Query("SELECT * FROM presets ORDER BY id DESC")
    @Transaction
    fun getAllPresets(): Flow<List<PresetWithExercises>>

    @Query("DELETE FROM presets WHERE id = :id")
    suspend fun deletePresetById(id: Long)

    @Update()
    suspend fun updatePreset(preset: Preset)

    @Insert()
    suspend fun insertExercise(exercise: PresetExercise)

    @Update()
    suspend fun updateExercise(exercise: PresetExercise)

    @Delete()
    suspend fun deleteExercise(exercise: PresetExercise)
}