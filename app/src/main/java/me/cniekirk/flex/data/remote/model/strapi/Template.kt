package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Template(
    @Json(name = "backgroundColor")
    val backgroundColor: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "richtext")
    val richtext: String?,
    @Json(name = "text")
    val text: String?,
    @Json(name = "textColor")
    val textColor: String?,
    @Json(name = "type")
    val type: String?
)