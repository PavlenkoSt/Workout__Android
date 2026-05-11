package com.stanislav_pav.repstation.data.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.stanislav_pav.repstation.data.models.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * from goals ORDER BY created_at desc")
    fun getAll(): Flow<List<Goal>>

    @Insert
    suspend fun create(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)
}