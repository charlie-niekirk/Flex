package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OutboundLink(
    @Json(name = "expiresAt")
    val expiresAt: String?,
    @Json(name = "url")
    val url: String?
)