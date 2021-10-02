package me.cniekirk.flex.data.remote.redgifs


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ContentUrls(
    val mobile: Mobile,
    val mobilePoster: MobilePoster,
    val mp4: Mp4,
    val poster: Poster,
    @Json(name = "100pxGif")
    val pxGif: PxGif
)