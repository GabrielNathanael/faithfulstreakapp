package com.faithfulstreak.app.v1.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

data class Ayat(
    val kitab: String,
    val pasal: Int,
    val ayat: Int,
    val firman: String
)

class VerseProvider(private val context: Context) {
    private val gson = Gson()
    private val verses by lazy { loadLocal() }

    private fun loadLocal(): List<Ayat> {
        val resId = context.resources.getIdentifier("ayat", "raw", context.packageName)
        val inputStream = context.resources.openRawResource(resId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val json = reader.use { it.readText() }

        val listType = object : TypeToken<List<Ayat>>() {}.type
        return gson.fromJson(json, listType)
    }

    fun random(): Ayat {
        if (verses.isEmpty()) {
            return Ayat("Kejadian", 1, 1, "Pada mulanya Allah menciptakan langit dan bumi.")
        }
        return verses[Random.nextInt(verses.size)]
    }
}
