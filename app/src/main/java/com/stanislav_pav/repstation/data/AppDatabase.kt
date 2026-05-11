package com.stanislav_pav.repstation.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stanislav_pav.repstation.data.daos.GoalDao
import com.stanislav_pav.repstation.data.daos.PresetDao
import com.stanislav_pav.repstation.data.daos.RecordDao
import com.stanislav_pav.repstation.data.daos.TrainingDayDao
import com.stanislav_pav.repstation.data.models.ExerciseTypeConverters
import com.stanislav_pav.repstation.data.models.ExerciseUnitsConverters
import com.stanislav_pav.repstation.data.models.Goal
import com.stanislav_pav.repstation.data.models.Preset
import com.stanislav_pav.repstation.data.models.PresetExercise
import com.stanislav_pav.repstation.data.models.RecordModel
import com.stanislav_pav.repstation.data.models.TrainingDay
import com.stanislav_pav.repstation.data.models.TrainingExercise

@Database(
    entities = [
        TrainingDay::class,
        TrainingExercise::class,
        Preset::class,
        PresetExercise::class,
        RecordModel::class,
        Goal::class
    ],
    version = 6,
    exportSchema = false
)
@TypeConverters(
    ExerciseTypeConverters::class,
    ExerciseUnitsConverters::class,
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
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

