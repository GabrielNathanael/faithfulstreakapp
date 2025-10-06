// app/src/main/java/com/faithfulstreak/app/v1/data/local/PrefsDataStore.kt
package com.faithfulstreak.app.v1.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.WeekFields

private val Context.dataStore by preferencesDataStore("streak_prefs")

object StreakKeys {
    val COUNT = intPreferencesKey("count")
    val LAST_CHECKIN = stringPreferencesKey("last_check_in") // ISO yyyy-MM-dd
    val TARGET = intPreferencesKey("target")
    val START_DATE = stringPreferencesKey("start_date")

    // Weekly progress
    val WEEK_KEY = stringPreferencesKey("week_key")           // e.g. "2025-41"
    val WEEK_DAYS = stringPreferencesKey("week_days")         // e.g. "1,2,3" (Mon=1..Sun=7)
}

data class PrefSnapshot(
    val count: Int,
    val last: LocalDate?,
    val target: Int,
    val start: LocalDate?,
    val weekKey: String?,
    val weekDays: Set<Int>,
)

class PrefsDataStore(private val context: Context) {

    val flow = context.dataStore.data.map { p: Preferences ->
        val count = p[StreakKeys.COUNT] ?: 0
        val last = p[StreakKeys.LAST_CHECKIN]?.let(LocalDate::parse)
        val target = p[StreakKeys.TARGET] ?: 7
        val start = p[StreakKeys.START_DATE]?.let(LocalDate::parse)

        val weekKey = p[StreakKeys.WEEK_KEY]
        val weekDays = parseDaysSet(p[StreakKeys.WEEK_DAYS])

        PrefSnapshot(
            count = count,
            last = last,
            target = target,
            start = start,
            weekKey = weekKey,
            weekDays = weekDays
        )
    }

    suspend fun setStreak(count: Int, last: LocalDate, target: Int, start: LocalDate) {
        context.dataStore.edit { p ->
            p[StreakKeys.COUNT] = count
            p[StreakKeys.LAST_CHECKIN] = last.toString()
            p[StreakKeys.TARGET] = target
            p[StreakKeys.START_DATE] = start.toString()
        }
    }

    suspend fun markCheckedToday(today: LocalDate) {
        val key = isoWeekKey(today)
        val day = today.dayOfWeek.value // Mon=1..Sun=7
        context.dataStore.edit { p ->
            val currentKey = p[StreakKeys.WEEK_KEY]
            val currentDays = parseDaysSet(p[StreakKeys.WEEK_DAYS]).toMutableSet()
            val newDays =
                if (currentKey == key) currentDays.apply { add(day) }
                else mutableSetOf(day)
            p[StreakKeys.WEEK_KEY] = key
            p[StreakKeys.WEEK_DAYS] = serializeDaysSet(newDays)
        }
    }

    suspend fun reset(last: LocalDate, keepTarget: Boolean, currentTarget: Int?) {
        context.dataStore.edit { p ->
            p[StreakKeys.COUNT] = 0
            p[StreakKeys.LAST_CHECKIN] = last.toString()
            p[StreakKeys.TARGET] = if (keepTarget) (currentTarget ?: 7) else 7
            p[StreakKeys.START_DATE] = last.toString()
            // reset weekly
            p[StreakKeys.WEEK_KEY] = isoWeekKey(last)
            p[StreakKeys.WEEK_DAYS] = serializeDaysSet(emptySet())
        }
    }

    private fun parseDaysSet(raw: String?): Set<Int> =
        raw?.split(",")?.mapNotNull { it.trim().toIntOrNull() }?.toSet() ?: emptySet()

    private fun serializeDaysSet(days: Set<Int>): String =
        days.sorted().joinToString(",")

    private fun isoWeekKey(date: LocalDate): String {
        val fields = WeekFields.ISO
        val ww = date.get(fields.weekOfWeekBasedYear())
        return "${date.year}-${ww}"
    }
}
