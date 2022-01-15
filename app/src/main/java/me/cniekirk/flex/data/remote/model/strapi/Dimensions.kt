package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Dimensions(
    @Json(name = "height")
    val height: Int?,
    @Json(name = "width")
    val width: Int?
)