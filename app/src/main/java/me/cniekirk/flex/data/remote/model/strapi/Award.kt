package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Award(
    @Json(name = "awardSubType")
    val awardSubType: String?,
    @Json(name = "awardType")
    val awardType: String?,
    @Json(name = "coinPrice")
    val coinPrice: Int?,
    @Json(name = "icon_128")
    val icon128: Icon?,
    @Json(name = "icon_16")
    val icon: Icon?,
    @Json(name = "icon_24")
    val icon24: Icon?,
    @Json(name = "icon_32")
    val icon32: Icon?,
    @Json(name = "icon_48")
    val icon48: Icon?,
    @Json(name = "icon_64")
    val icon64: Icon?,
    @Json(name = "icon_72")
    val icon72: Icon?,
    @Json(name = "icon_96")
    val icon96: Icon?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "tiers")
    val tiers: Any?
)