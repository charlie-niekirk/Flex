package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.reddit.redgifs.GfycatLinks
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RedGifsApi {

    @GET("v1/gfycats/{gfyid}")
    suspend fun getDirectLinks(@Path("gfyid") gfyid: String): Response<GfycatLinks>

}