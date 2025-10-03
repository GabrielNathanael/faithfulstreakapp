package com.faithfulstreak.app.v1.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    suspend fun insert(history: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY id DESC")
    suspend fun getAll(): List<HistoryEntity>
}
