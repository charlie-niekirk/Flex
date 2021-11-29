package me.cniekirk.flex.data.remote.redgifs


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaItem(
    val height: Int?,
    val size: Int?,
    val url: String?,
    val width: Int?
)