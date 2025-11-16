package com.learning.workout__android.data.repositories

import com.learning.workout__android.data.daos.PresetDao
import com.learning.workout__android.data.models.Preset
import com.learning.workout__android.data.models.PresetExercise
import com.learning.workout__android.data.models.PresetWithExercises
import kotlinx.coroutines.flow.Flow

class PresetsRepository(
    private val presetDao: PresetDao
) {
    fun getAllPresets(): Flow<List<PresetWithExercises>> {
        return presetDao.getAllPresets()
    }

    fun getPresetByIdWithExercises(presetId: Long): Flow<PresetWithExercises?> {
        return presetDao.getPresetByIdWithExercises(presetId)
    }

    suspend fun createPreset(preset: Preset) {
        presetDao.insertPreset(preset)
    }

    suspend fun updatePreset(preset: Preset) {
        presetDao.updatePreset(preset)
    }

    suspend fun deletePreset(preset: Preset) {
        presetDao.deletePreset(preset)
    }

    suspend fun addExercise(exercise: PresetExercise) {
        val exercises = presetDao.getExercisesByPresetId(exercise.presetId)
        val maxOrder = exercises.maxOfOrNull { it.order } ?: -1
        presetDao.insertExercise(exercise.copy(order = maxOrder + 1))
    }

    suspend fun updateExercise(exercise: PresetExercise) {
        presetDao.updateExercise(exercise)
    }

    suspend fun deleteExercise(exercise: PresetExercise) {
        presetDao.deleteExercise(exercise)
    }

    suspend fun reorderPresets(fromPresetId: Long, toPresetId: Long) {
        presetDao.swapPresetOrder(fromPresetId, toPresetId)
    }

    suspend fun reorderExercises(fromExerciseId: Long, toExerciseId: Long) {
        presetDao.swapExercisesOrder(fromExerciseId, toExerciseId)
    }
}