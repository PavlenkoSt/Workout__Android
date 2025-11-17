package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(
    tableName = "training_exercises",
    foreignKeys = [
        ForeignKey(
            entity = TrainingDay::class,
            parentColumns = ["id"],
            childColumns = ["trainingDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("trainingDayId")]
)
data class TrainingExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "trainingDayId") val trainingDayId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "sets") val sets: Int,
    @ColumnInfo(name = "rest") val rest: Int,
    @ColumnInfo(name = "type") val type: ExerciseType,
    @ColumnInfo(name = "setsDone") val setsDone: Int,
    @ColumnInfo(name = "order") val order: Int = 0
)

enum class ExerciseType(val label: String) {
    DYNAMIC("dynamic"),
    STATIC("static"),
    LADDER("ladder"),
    WARMUP("warmup"),
    FLEXIBILITY_SESSION("flexibility_session"),
    HAND_BALANCE_SESSION("hand_balance_session")
}

class ExerciseConverters {
    @TypeConverter
    fun fromType(type: ExerciseType): String = type.name

    @TypeConverter
    fun toType(value: String): ExerciseType = ExerciseType.valueOf(value)
}
