package com.faithfulstreak.app.v1.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY id DESC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentStreak(): HistoryEntity?

    @Insert
    suspend fun insert(entity: HistoryEntity)

    @Update  // <- ini yang error, pastikan import androidx.room.Update ada
    suspend fun update(entity: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}