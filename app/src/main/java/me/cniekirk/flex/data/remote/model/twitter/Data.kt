package me.cniekirk.flex.data.remote.model.twitter


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "attachments")
    val attachments: Attachments?,
    @Json(name = "author_id")
    val authorId: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "text")
    val text: String?
)