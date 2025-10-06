package com.faithfulstreak.app.v1.data.remote

import com.faithfulstreak.app.v1.model.Verse
import retrofit2.http.GET

interface BibleApi {
    @GET("random") // placeholder; nanti bisa di-point ke service yang lo pilih
    suspend fun random(): Verse
}
