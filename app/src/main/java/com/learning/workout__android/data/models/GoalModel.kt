package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter


@Entity(
    tableName = "goals"
)
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "count") val count: Int = 0,
    @ColumnInfo(name = "targetCount") val targetCount: Int,
    @ColumnInfo(name = "units") val units: GoalUnits,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
) {
    val status
        get() = if (count >= targetCount) {
            GoalsStatusEnum.Completed
        } else GoalsStatusEnum.Pending
}

class GoalConverters {
    @TypeConverter
    fun fromUnits(units: GoalUnits): String = units.name

    @TypeConverter
    fun toUnits(name: String): GoalUnits = GoalUnits.valueOf(name)
}

enum class GoalUnits(val label: String) {
    REPS("reps"),
    SEC("sec"),
    MIN("min"),
    KM("km")
}

enum class GoalsStatusEnum {
    Completed,
    Pending
}