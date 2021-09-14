package me.cniekirk.flex.data.remote.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RedditVideo(
    @Json(name = "fallback_url") val fallbackUrl: String,
    @Json(name = "dash_url") val dashUrl: String?,
    @Json(name = "hls_url") val hlsUrl: String?,
    @Json(name = "is_gif") val isGif: Boolean
) : Parcelable