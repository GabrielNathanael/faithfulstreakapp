package com.faithfulstreak.app.v1.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.faithfulstreak.app.v1.util.Ayat
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.WeekFields

private val Context.dataStore by preferencesDataStore("streak_prefs")

object StreakKeys {
    val COUNT = intPreferencesKey("count")
    val LAST_CHECKIN = stringPreferencesKey("last_check_in")
    val TARGET = intPreferencesKey("target")
    val START_DATE = stringPreferencesKey("start_date")
    val WEEK_KEY = stringPreferencesKey("week_key")
    val WEEK_DAYS = stringPreferencesKey("week_days")
    val BYPASS_TEST = booleanPreferencesKey("bypass_test_mode")

    // ✅ tambahan: penyimpanan ayat
    val VERSE_KITAB = stringPreferencesKey("verse_kitab")
    val VERSE_PASAL = intPreferencesKey("verse_pasal")
    val VERSE_AYAT = intPreferencesKey("verse_ayat")
    val VERSE_TEXT = stringPreferencesKey("verse_text")
}

data class PrefSnapshot(
    val count: Int,
    val last: LocalDate?,
    val target: Int,
    val start: LocalDate?,
    val weekKey: String?,
    val weekDays: Set<Int>,
    val bypass: Boolean = false,
    val verse: Ayat = Ayat("Kejadian", 1, 1, "Pada mulanya Allah menciptakan langit dan bumi.")
)

class PrefsDataStore(private val context: Context) {

    val flow = context.dataStore.data.map { p ->
        val count = p[StreakKeys.COUNT] ?: 0
        val last = p[StreakKeys.LAST_CHECKIN]?.let(LocalDate::parse)
        val target = p[StreakKeys.TARGET] ?: 7
        val start = p[StreakKeys.START_DATE]?.let(LocalDate::parse)
        val weekKey = p[StreakKeys.WEEK_KEY]
        val weekDays = parseDaysSet(p[StreakKeys.WEEK_DAYS])
        val bypass = p[StreakKeys.BYPASS_TEST] ?: false

        val verse = Ayat(
            kitab = p[StreakKeys.VERSE_KITAB] ?: "Kejadian",
            pasal = p[StreakKeys.VERSE_PASAL] ?: 1,
            ayat = p[StreakKeys.VERSE_AYAT] ?: 1,
            firman = p[StreakKeys.VERSE_TEXT]
                ?: "Pada mulanya Allah menciptakan langit dan bumi."
        )

        PrefSnapshot(count, last, target, start, weekKey, weekDays, bypass, verse)
    }

    suspend fun setBypassTesting(enabled: Boolean) {
        context.dataStore.edit { it[StreakKeys.BYPASS_TEST] = enabled }
    }

    suspend fun setVerse(ayat: Ayat) {
        context.dataStore.edit { p ->
            p[StreakKeys.VERSE_KITAB] = ayat.kitab
            p[StreakKeys.VERSE_PASAL] = ayat.pasal
            p[StreakKeys.VERSE_AYAT] = ayat.ayat
            p[StreakKeys.VERSE_TEXT] = ayat.firman
        }
    }

    suspend fun setStreak(count: Int, last: LocalDate, target: Int, start: LocalDate) {
        context.dataStore.edit {
            it[StreakKeys.COUNT] = count
            it[StreakKeys.LAST_CHECKIN] = last.toString()
            it[StreakKeys.TARGET] = target
            it[StreakKeys.START_DATE] = start.toString()
        }
    }

    suspend fun markCheckedToday(today: LocalDate, bypassMode: Boolean = false) {
        val key = isoWeekKey(today)
        val dayOfWeek = today.dayOfWeek.value // 1..7

        context.dataStore.edit { p ->
            val currentKey = p[StreakKeys.WEEK_KEY]
            val currentDays = parseDaysSet(p[StreakKeys.WEEK_DAYS]).toMutableSet()

            val newDays = when {
                // minggu baru → reset
                currentKey != key -> mutableSetOf(dayOfWeek)
                // minggu lama tapi penuh → reset di check-in berikutnya
                currentDays.size >= 7 -> mutableSetOf(dayOfWeek)
                else -> currentDays.apply {
                    if (bypassMode) {
                        // kalau masih kosong (baru relapse), mulai dari hari real
                        if (isEmpty()) {
                            add(dayOfWeek)
                        } else {
                            // kalau udah ada isi, lanjut ke hari berikutnya
                            val lastDay = maxOrNull() ?: 0
                            val nextDay = if (lastDay < 7) lastDay + 1 else 1
                            if (size < 7) add(nextDay)
                        }
                    } else {
                        add(dayOfWeek)
                    }
                }
            }

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
            p[StreakKeys.WEEK_KEY] = isoWeekKey(last)
            p[StreakKeys.WEEK_DAYS] = serializeDaysSet(emptySet())

            // juga reset ayat ke default
            p[StreakKeys.VERSE_KITAB] = "1 Korintus"
            p[StreakKeys.VERSE_PASAL] = 10
            p[StreakKeys.VERSE_AYAT] = 23
            p[StreakKeys.VERSE_TEXT] = "\"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu berguna. \"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu membangun."
        }
    }

    private fun parseDaysSet(raw: String?) =
        raw?.split(",")?.mapNotNull { it.trim().toIntOrNull() }?.toSet() ?: emptySet()

    private fun serializeDaysSet(days: Set<Int>) = days.sorted().joinToString(",")

    private fun isoWeekKey(date: LocalDate): String {
        val ww = date.get(WeekFields.ISO.weekOfWeekBasedYear())
        return "${date.year}-${ww}"
    }
}
