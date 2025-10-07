package com.faithfulstreak.app.v1.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startDate: String, // yyyy-MM-dd
    val endDate: String,   // yyyy-MM-dd
    val length: Int,       // jumlah hari streak
    val type: String       // "Check-in" atau "Relapse"
)
