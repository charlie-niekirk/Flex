package me.cniekirk.flex.data.remote.imgur


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "account_id")
    val accountId: String?,
    @Json(name = "account_url")
    val accountUrl: String?,
    @Json(name = "ad_config")
    val adConfig: AdConfig?,
    @Json(name = "ad_type")
    val adType: Int?,
    @Json(name = "ad_url")
    val adUrl: String?,
    val animated: Boolean?,
    val bandwidth: Int?,
    val datetime: Int?,
    val description: String?,
    val edited: String?,
    val favorite: Boolean?,
    @Json(name = "has_sound")
    val hasSound: Boolean?,
    val height: Int?,
    val id: String?,
    @Json(name = "in_gallery")
    val inGallery: Boolean?,
    @Json(name = "in_most_viral")
    val inMostViral: Boolean?,
    @Json(name = "is_ad")
    val isAd: Boolean?,
    val link: String?,
    val nsfw: Boolean?,
    val section: String?,
    val size: Int?,
    val tags: @RawValue List<Any>?,
    val title: String?,
    val type: String?,
    val views: Int?,
    val vote: Long?,
    val width: Int?
) : Parcelable