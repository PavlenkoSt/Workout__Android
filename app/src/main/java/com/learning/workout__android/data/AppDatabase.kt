package com.learning.workout__android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.learning.workout__android.data.daos.TrainingDayDao
import com.learning.workout__android.data.models.Converters
import com.learning.workout__android.data.models.Exercise
import com.learning.workout__android.data.models.TrainingDay

@Database(entities = [TrainingDay::class, Exercise::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trainingDayDao(): TrainingDayDao

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