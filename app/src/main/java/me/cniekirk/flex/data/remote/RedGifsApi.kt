package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.redgifs.RedGifLinks
import retrofit2.http.GET
import retrofit2.http.Path

interface RedGifsApi {

    @GET("v1/gfycats/{gyfId}")
    suspend fun getDirectLinks(@Path("gyfId") gyfId: String): RedGifLinks

}