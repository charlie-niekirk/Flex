package me.cniekirk.flex.data.remote.model.reddit

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class OEmbed(
    @Json(name = "provider_url") val providerUrl: String?,
    val version: String?,
    val title: String?,
    val type: String?,
    @Json(name = "provider_name") val providerName: String?,
    @Json(name = "thumbnail_url") val thumbnailUrl: String?,
) : Parcelable
