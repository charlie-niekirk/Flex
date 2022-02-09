package me.cniekirk.flex.data.remote.model.twitter


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TweetResponse(
    @Json(name = "data")
    val data: Data?,
    @Json(name = "includes")
    val includes: Includes?
)