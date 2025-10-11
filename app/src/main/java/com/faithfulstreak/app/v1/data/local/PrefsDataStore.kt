package com.faithfulstreak.app.v1.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.faithfulstreak.app.v1.util.Ayat
import com.faithfulstreak.app.v1.util.AyatDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.map
import java.time.LocalDate

private val Context.dataStore by preferencesDataStore("streak_prefs")

object StreakKeys {
    val COUNT = intPreferencesKey("count")
    val LAST_CHECKIN = stringPreferencesKey("last_check_in")
    val TARGET = intPreferencesKey("target")
    val START_DATE = stringPreferencesKey("start_date")
    val WEEK_KEY = stringPreferencesKey("week_key")
    val WEEK_DAYS = stringPreferencesKey("week_days")
    val BYPASS_TEST = booleanPreferencesKey("bypass_test_mode")

    val VERSE_KITAB = stringPreferencesKey("verse_kitab")
    val VERSE_PASAL = intPreferencesKey("verse_pasal")
    val VERSE_ayatMulai = intPreferencesKey("verse_ayatMulai")
    val VERSE_ayatSelesai = intPreferencesKey("verse_ayatSelesai")
    val VERSE_TEXT = stringPreferencesKey("verse_text")
    val VERSE_DETAIL = stringPreferencesKey("verse_detail") // JSON string
}

data class PrefSnapshot(
    val count: Int,
    val last: LocalDate?,
    val target: Int,
    val start: LocalDate?,
    val weekKey: String?,
    val weekDays: Set<Int>,
    val bypass: Boolean = false,
    val verse: Ayat = Ayat(
        "1 Korintus", 10, 23, 23,
        "\"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu berguna. \"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu membangun.",
        listOf(AyatDetail(23, "\"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu berguna. \"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu membangun."))
    )
)

class PrefsDataStore(private val context: Context) {
    private val gson = Gson()

    val flow = context.dataStore.data.map { p ->
        val count = p[StreakKeys.COUNT] ?: 0
        val last = p[StreakKeys.LAST_CHECKIN]?.let(LocalDate::parse)
        val target = p[StreakKeys.TARGET] ?: 0
        val start = p[StreakKeys.START_DATE]?.let(LocalDate::parse)
        val weekKey = p[StreakKeys.WEEK_KEY]
        val weekDays = parseDaysSet(p[StreakKeys.WEEK_DAYS])
        val bypass = p[StreakKeys.BYPASS_TEST] ?: false

        // Parse detailAyat from JSON
        val detailAyatJson = p[StreakKeys.VERSE_DETAIL]
        val detailAyat = if (detailAyatJson != null) {
            try {
                val listType = object : TypeToken<List<AyatDetail>>() {}.type
                gson.fromJson<List<AyatDetail>>(detailAyatJson, listType) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        val verse = Ayat(
            kitab = p[StreakKeys.VERSE_KITAB] ?: "1 Korintus",
            pasal = p[StreakKeys.VERSE_PASAL] ?: 10,
            ayatMulai = p[StreakKeys.VERSE_ayatMulai] ?: 23,
            ayatSelesai = p[StreakKeys.VERSE_ayatSelesai] ?: 23,
            firman = p[StreakKeys.VERSE_TEXT]
                ?: "\"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu berguna. \"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu membangun.",
            detailAyat = detailAyat
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
            p[StreakKeys.VERSE_ayatMulai] = ayat.ayatMulai
            p[StreakKeys.VERSE_ayatSelesai] = ayat.ayatSelesai
            p[StreakKeys.VERSE_TEXT] = ayat.firman
            // Serialize detailAyat to JSON
            p[StreakKeys.VERSE_DETAIL] = gson.toJson(ayat.detailAyat)
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
        context.dataStore.edit { p ->
            val startDate = p[StreakKeys.START_DATE]?.let(LocalDate::parse) ?: today
            val currentKey = p[StreakKeys.WEEK_KEY]
            val currentDays = parseDaysSet(p[StreakKeys.WEEK_DAYS]).toMutableSet()

            val dayOfWeek = today.dayOfWeek.value // 1..7

            // --- Tentukan newDays dan finalWeekKey ---
            val newDays: MutableSet<Int>
            val finalWeekKey: String

            if (bypassMode) {
                // BYPASS MODE: simulasi 1-7 loop terus
                when {
                    currentDays.isEmpty() -> {
                        newDays = mutableSetOf(1)
                        finalWeekKey = "week-0"
                    }
                    currentDays.size >= 7 -> {
                        // Reset ke hari 1, naikin week counter
                        newDays = mutableSetOf(1)
                        val currentWeekNum = currentKey?.removePrefix("week-")?.toIntOrNull() ?: 0
                        finalWeekKey = "week-${currentWeekNum + 1}"
                    }
                    else -> {
                        // Tambah hari berikutnya
                        val maxDay = currentDays.maxOrNull() ?: 0
                        newDays = currentDays.apply { add(maxDay + 1) }
                        finalWeekKey = currentKey ?: "week-0"
                    }
                }
            } else {
                // MODE NORMAL: per 7 hari (bukan per minggu kalender)
                val daysSinceStart = java.time.temporal.ChronoUnit.DAYS.between(startDate, today)
                val weekNumber = (daysSinceStart / 7).toInt()
                val newKey = "week-$weekNumber"

                if (currentKey != newKey) {
                    // Udah lewat 7 hari → reset
                    newDays = mutableSetOf(dayOfWeek)
                    finalWeekKey = newKey
                } else {
                    // Masih dalam 7 hari yang sama, tambah hari
                    newDays = currentDays.apply { add(dayOfWeek) }
                    finalWeekKey = newKey
                }
            }

            // --- Simpan hasil ---
            p[StreakKeys.WEEK_KEY] = finalWeekKey
            p[StreakKeys.WEEK_DAYS] = serializeDaysSet(newDays)
        }
    }

    suspend fun reset(last: LocalDate, keepTarget: Boolean, currentTarget: Int?) {
        context.dataStore.edit { p ->
            p[StreakKeys.COUNT] = 0
            p[StreakKeys.LAST_CHECKIN] = last.toString()
            p[StreakKeys.TARGET] = if (keepTarget) (currentTarget ?: 0) else 0
            p[StreakKeys.START_DATE] = last.toString()
            p[StreakKeys.WEEK_KEY] = "week-0"
            p[StreakKeys.WEEK_DAYS] = serializeDaysSet(emptySet())

            // juga reset ayat ke default
            val defaultDetail = listOf(
                AyatDetail(23, "\"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu berguna. \"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu membangun.")
            )
            p[StreakKeys.VERSE_KITAB] = "1 Korintus"
            p[StreakKeys.VERSE_PASAL] = 10
            p[StreakKeys.VERSE_ayatMulai] = 23
            p[StreakKeys.VERSE_ayatSelesai] = 23
            p[StreakKeys.VERSE_TEXT] = "\"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu berguna. \"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu membangun."
            p[StreakKeys.VERSE_DETAIL] = gson.toJson(defaultDetail)
        }
    }

    private fun parseDaysSet(raw: String?) =
        raw?.split(",")?.mapNotNull { it.trim().toIntOrNull() }?.toSet() ?: emptySet()

    private fun serializeDaysSet(days: Set<Int>) = days.sorted().joinToString(",")
}