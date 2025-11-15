package com.learning.workout__android.data.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate

@Entity(
    tableName = "training_days",
    indices = [Index(value = ["date"], unique = true)]
)
data class TrainingDay(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String
)

data class TrainingDayWithExercises(
    @Embedded val trainingDay: TrainingDay,
    @Relation(
        parentColumn = "id",
        entityColumn = "trainingDayId"
    )
    val exercises: List<TrainingExercise>
) {
    val sortedExercises: List<TrainingExercise>
        get() = exercises.sortedBy { it.order }
    val status: TrainingDayStatus
        get() {
            return if(exercises.isNotEmpty() && exercises.all { it.setsDone >= it.sets }) {
                TrainingDayStatus.Completed
            }else if (LocalDate.now().isAfter(LocalDate.parse(trainingDay.date))) {
                TrainingDayStatus.Failed
            }else {
                TrainingDayStatus.Pending
            }
        }
}

enum class TrainingDayStatus() {
    Completed,
    Pending,
    Failed
}