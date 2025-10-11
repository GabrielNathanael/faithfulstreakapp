package com.faithfulstreak.app.v1.data.local

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun db(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "faithful_streak_db"
            )
                .fallbackToDestructiveMigration() // <- tambah ini aja
                .build()
                .also { INSTANCE = it }
        }
    }
}