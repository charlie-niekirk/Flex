package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.reddit.streamable.StreamableVideo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StreamableApi {

    @GET("videos/{shortcode}")
    suspend fun getStreamableDetails(@Path("shortcode") shortcode: String): Response<StreamableVideo>

}