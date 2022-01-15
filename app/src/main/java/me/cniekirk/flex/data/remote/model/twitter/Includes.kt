package me.cniekirk.flex.data.remote.model.twitter


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Includes(
    @Json(name = "media")
    val media: List<Media>?,
    @Json(name = "users")
    val users: List<User>?
)