package com.faithfulstreak.app.v1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: String,      // yyyy-MM-dd (first check-in)
    val endDate: String,        // yyyy-MM-dd (last check-in or relapse date)
    val streakLength: Int,      // jumlah hari streak
    val isCurrent: Boolean = false  // true = streak masih jalan, false = udah relapse
)