package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostFlairSettings(
    @Json(name = "isEnabled")
    val isEnabled: Boolean?,
    @Json(name = "position")
    val position: String?
)