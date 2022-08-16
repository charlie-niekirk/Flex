package me.cniekirk.flex.data.remote

import me.cniekirk.flex.data.remote.model.flex.Device
import me.cniekirk.flex.ui.settings.model.SubredditTracker
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface FlexApi {

    @POST("api/v1/watcher")
    suspend fun submitTracker(subredditTracker: SubredditTracker): Response<SubredditTracker>

    @GET("api/v1/watcher")
    suspend fun getTrackers(device: Device): Response<List<SubredditTracker>>
}