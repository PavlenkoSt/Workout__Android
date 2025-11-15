package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "preset_exercises",
    foreignKeys = [
        ForeignKey(
            entity = Preset::class,
            parentColumns = ["id"],
            childColumns = ["presetId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("presetId")]
)
data class PresetExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "presetId") val presetId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "sets") val sets: Int,
    @ColumnInfo(name = "rest") val rest: Int,
    @ColumnInfo(name = "type") val type: ExerciseType,
    @ColumnInfo(name = "order") val order: Int = 0
)