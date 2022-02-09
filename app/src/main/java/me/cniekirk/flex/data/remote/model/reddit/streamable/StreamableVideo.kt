package me.cniekirk.flex.data.remote.model.reddit.streamable


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class StreamableVideo(
    @Json(name = "embed_code")
    val embedCode: String?,
    val files: @RawValue Map<String, VideoType>?,
    val message: String?,
    val percent: Int?,
    val source: String?,
    val status: Int?,
    @Json(name = "thumbnail_url")
    val thumbnailUrl: String?,
    val title: String?,
    val url: String?
) : Parcelable