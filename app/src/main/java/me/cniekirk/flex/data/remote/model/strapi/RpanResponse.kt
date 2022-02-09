package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RpanResponse(
    @Json(name = "data")
    val `data`: List<Data>?,
    @Json(name = "next_cursor")
    val nextCursor: String?,
    @Json(name = "status")
    val status: String?,
    @Json(name = "status_message")
    val statusMessage: String?
)