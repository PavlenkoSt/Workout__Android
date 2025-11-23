package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "goals"
)
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "count") val count: Int = 0,
    @ColumnInfo(name = "targetCount") val targetCount: Int,
    @ColumnInfo(name = "units") val units: ExerciseUnits,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    val status
        get() = if (count >= targetCount) {
            GoalsStatusEnum.Completed
        } else GoalsStatusEnum.Pending
}

enum class GoalsStatusEnum {
    Completed,
    Pending
}