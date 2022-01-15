package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LegacyIcon(
    @Json(name = "dimensions")
    val dimensions: Dimensions?,
    @Json(name = "url")
    val url: String?
)