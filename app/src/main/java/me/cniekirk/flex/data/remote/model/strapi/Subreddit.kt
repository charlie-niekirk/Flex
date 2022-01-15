package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Subreddit(
    @Json(name = "id")
    val id: String?,
    @Json(name = "isFreeFormReportingAllowed")
    val isFreeFormReportingAllowed: Boolean?,
    @Json(name = "isNSFW")
    val isNSFW: Boolean?,
    @Json(name = "isQuarantined")
    val isQuarantined: Boolean?,
    @Json(name = "isThumbnailsEnabled")
    val isThumbnailsEnabled: Boolean?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "originalContentCategories")
    val originalContentCategories: List<Any>?,
    @Json(name = "path")
    val path: String?,
    @Json(name = "postFlairSettings")
    val postFlairSettings: PostFlairSettings?,
    @Json(name = "prefixedName")
    val prefixedName: String?,
    @Json(name = "styles")
    val styles: Styles?,
    @Json(name = "subscribers")
    val subscribers: Double?,
    @Json(name = "title")
    val title: String?,
    @Json(name = "type")
    val type: String?,
    @Json(name = "__typename")
    val typename: String?,
    @Json(name = "wls")
    val wls: String?
)