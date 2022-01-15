package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Flair(
    @Json(name = "richtext")
    val richtext: String?,
    @Json(name = "template")
    val template: Template?,
    @Json(name = "text")
    val text: String?,
    @Json(name = "textColor")
    val textColor: String?,
    @Json(name = "type")
    val type: String?
)