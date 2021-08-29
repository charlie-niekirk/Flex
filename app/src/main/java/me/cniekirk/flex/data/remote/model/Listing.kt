package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Listing(
    val after: String?,
    val before: String?,
    val children: List<Children>,
    val dist: Int,
    @Json(name = "geo_filter")
    val geoFilter: String?,
    val modhash: String
)