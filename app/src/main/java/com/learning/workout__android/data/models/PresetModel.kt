package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "presets",
    indices = [Index(value = ["id"], unique = true)]
)
data class Preset(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "order") val order: Long
)

data class PresetWithExercises(
    @Embedded val preset: Preset,
    @Relation(
        parentColumn = "id",
        entityColumn = "presetId"
    )
    val exercises: List<PresetExercise>
) {
    val sortedExercises: List<PresetExercise>
        get() = exercises.sortedBy { it.order }
}