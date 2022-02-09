package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Media(
    @Json(name = "streaming")
    val streaming: Any?,
    @Json(name = "typeHint")
    val typeHint: String?
)