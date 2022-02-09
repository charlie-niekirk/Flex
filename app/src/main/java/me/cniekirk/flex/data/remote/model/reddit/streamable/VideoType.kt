package me.cniekirk.flex.data.remote.model.reddit.streamable


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoType(
    val bitrate: Int?,
    val duration: Double?,
    val framerate: Double?,
    val height: Int?,
    val size: Int?,
    val status: Int?,
    val url: String?,
    val width: Int?
)