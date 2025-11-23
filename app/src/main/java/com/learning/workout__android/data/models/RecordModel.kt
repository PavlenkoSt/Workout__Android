package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "records"
)
data class RecordModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "count") val count: Int,
    @ColumnInfo(name = "units") val units: ExerciseUnits,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
