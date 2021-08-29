package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaEmbed(
    val content: String?,
    val height: Int?,
    val scrolling: Boolean?,
    val width: Int?
)