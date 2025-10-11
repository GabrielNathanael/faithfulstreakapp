package com.faithfulstreak.app.v1.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random
import com.faithfulstreak.app.R

data class AyatDetail(
    val ayat: Int,
    val teks: String
)

data class Ayat(
    val kitab: String,
    val pasal: Int,
    val ayatMulai: Int,
    val ayatSelesai: Int,
    val firman: String,
    val detailAyat: List<AyatDetail> = emptyList()
) {
    // Helper function untuk display reference
    fun referenceString(): String {
        return if (ayatMulai == ayatSelesai) {
            "$kitab $pasal:$ayatMulai"
        } else {
            "$kitab $pasal:$ayatMulai-$ayatSelesai"
        }
    }

    // Check if single verse
    fun isSingleVerse(): Boolean = ayatMulai == ayatSelesai
}

class VerseProvider(private val context: Context) {
    private val gson = Gson()
    private val verses by lazy { loadLocal() }

    private fun loadLocal(): List<Ayat> {
        val inputStream = context.resources.openRawResource(R.raw.ayat)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.use { it.readText() }

        val listType = object : TypeToken<List<Ayat>>() {}.type
        return gson.fromJson(json, listType)
    }

    fun random(): Ayat {
        if (verses.isEmpty()) {
            return Ayat(
                "1 Korintus", 10, 23, 23,
                "\"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu berguna. \"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu membangun.",
                listOf(AyatDetail(23, "\"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu berguna. \"Segala sesuatu diperbolehkan.\" Benar, tetapi bukan segala sesuatu membangun."))
            )
        }
        return verses[Random.nextInt(verses.size)]
    }

    @Suppress("unused")
    fun randomVerse(): Ayat = random()

    fun randomSingleVerse(): Ayat {
        val singleVerses = verses.filter { it.ayatMulai == it.ayatSelesai }
        if (singleVerses.isEmpty()) {
            return Ayat(
                "1 Korintus", 10, 13, 13,
                "Pencobaan-pencobaan yang kamu alami ialah pencobaan-pencobaan biasa yang tidak melebihi kekuatan manusia. Sebab Allah setia dan karena itu Ia tidak akan membiarkan kamu dicobai melampaui kekuatanmu. Pada waktu kamu dicobai Ia akan memberikan kepadamu jalan ke luar sehingga kamu dapat menanggungnya.",
                listOf(AyatDetail(13, "Pencobaan-pencobaan yang kamu alami ialah pencobaan-pencobaan biasa yang tidak melebihi kekuatan manusia. Sebab Allah setia dan karena itu Ia tidak akan membiarkan kamu dicobai melampaui kekuatanmu. Pada waktu kamu dicobai Ia akan memberikan kepadamu jalan ke luar sehingga kamu dapat menanggungnya."))
            )
        }
        return singleVerses[Random.nextInt(singleVerses.size)]
    }
}