package me.cniekirk.flex.data.remote.model.rules


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Rule(
    @Json(name = "created_utc")
    val createdUtc: Double?,
    val description: String?,
    @Json(name = "description_html")
    val descriptionHtml: String?,
    val kind: String?,
    val priority: Int?,
    @Json(name = "short_name")
    val shortName: String?,
    @Json(name = "violation_reason")
    val violationReason: String?
)