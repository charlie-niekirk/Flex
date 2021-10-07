package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.redgifs.GfycatLinks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GfycatApi {

    @GET("v1/gfycats/{gfyid}")
    suspend fun getGfycatLinks(@Path("gfyid") gfyid: String): Response<GfycatLinks>

}