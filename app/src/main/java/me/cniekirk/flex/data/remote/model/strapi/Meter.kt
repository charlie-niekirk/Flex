package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meter(
    @Json(name = "enabled")
    val enabled: Boolean?,
    @Json(name = "full_meter_duration")
    val fullMeterDuration: Int?,
    @Json(name = "proportion_full")
    val proportionFull: Double?
)