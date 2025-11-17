package com.learning.workout__android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.learning.workout__android.data.daos.GoalDao
import com.learning.workout__android.data.daos.PresetDao
import com.learning.workout__android.data.daos.RecordDao
import com.learning.workout__android.data.daos.TrainingDayDao
import com.learning.workout__android.data.models.ExerciseConverters
import com.learning.workout__android.data.models.Goal
import com.learning.workout__android.data.models.GoalConverters
import com.learning.workout__android.data.models.Preset
import com.learning.workout__android.data.models.PresetExercise
import com.learning.workout__android.data.models.RecordConverters
import com.learning.workout__android.data.models.RecordModel
import com.learning.workout__android.data.models.TrainingDay
import com.learning.workout__android.data.models.TrainingExercise

@Database(
    entities = [
        TrainingDay::class,
        TrainingExercise::class,
        Preset::class,
        PresetExercise::class,
        RecordModel::class,
        Goal::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(
    ExerciseConverters::class,
    RecordConverters::class,
    GoalConverters::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDayDao(): TrainingDayDao
    abstract fun presetDao(): PresetDao
    abstract fun recordDao(): RecordDao
    abstract fun goalDao(): GoalDao

    companion object {
        private var Instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "workout_database")
                    .fallbackToDestructiveMigration(false) // For development - in production use proper migrations
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

