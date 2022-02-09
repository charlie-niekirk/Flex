package me.cniekirk.flex.data.remote.model.twitter


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Attachments(
    @Json(name = "media_keys")
    val mediaKeys: List<String>?
)