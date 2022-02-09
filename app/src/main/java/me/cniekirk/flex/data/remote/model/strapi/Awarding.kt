package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Awarding(
    @Json(name = "award")
    val award: Award?,
    @Json(name = "total")
    val total: Int?
)