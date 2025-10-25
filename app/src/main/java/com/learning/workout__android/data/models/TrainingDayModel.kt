package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import androidx.room.TypeConverter

data class TrainingDayWithExercises (
    @Embedded val trainingDay: TrainingDay,
    @Relation(
        parentColumn = "id",
        entityColumn = "trainingDayId"
    )
    val exercises: List<Exercise>
)

@Entity(tableName = "training_days")
data class TrainingDay (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date") val date: String
)

@Entity(
    tableName = "exercises",
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
data class Exercise (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "trainingDayId") val trainingDayId: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "sets") val sets: Int,
    @ColumnInfo(name = "rest") val rest: Int,
    @ColumnInfo(name = "type") val type: ExerciseType,
    @ColumnInfo(name = "setsDone") val setsDone: Int
)

enum class ExerciseType(val label: String) {
    DYNAMIC("dynamic"),
    STATIC("static"),
    LADDER("ladder"),
    WARMUP("warmup"),
    FLEXIBILITY_SESSION("flexibility_session"),
    HAND_BALANCE_SESSION("hand_balance_session")
}

class Converters {
    @TypeConverter
    fun fromType(type: ExerciseType): String = type.name
    @TypeConverter fun toType(value: String): ExerciseType = ExerciseType.valueOf(value)
}