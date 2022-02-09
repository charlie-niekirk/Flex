package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Styles(
    @Json(name = "icon")
    val icon: String?,
    @Json(name = "legacyIcon")
    val legacyIcon: LegacyIcon?,
    @Json(name = "primaryColor")
    val primaryColor: String?
)