package me.cniekirk.flex.data.remote.model.twitter


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Media(
    @Json(name = "media_key")
    val mediaKey: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "url")
    val url: String?
)