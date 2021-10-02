package me.cniekirk.flex.data.remote.redgifs


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GfyItem(
    val avgColor: String,
    val captionsUrl: Any,
    @Json(name = "content_urls")
    val contentUrls: ContentUrls,
    val createDate: Int,
    val dislikes: Int,
    val domainWhitelist: List<Any>,
    val duration: Int,
    val encoding: Boolean,
    val finished: Boolean,
    val frameRate: Int,
    val gatekeeper: Int,
    val geoWhitelist: List<Any>,
    val gfyId: String,
    val gfyName: String,
    val gifUrl: String,
    val hasAudio: Boolean,
    val hasTransparency: Boolean,
    val height: Int,
    val languageCategories: List<Any>,
    val likes: Int,
    val max2mbGif: String,
    val max5mbGif: String,
    val miniPosterUrl: String,
    val mobileHeight: Int,
    val mobilePosterUrl: String,
    val mobileUrl: String,
    val mobileWidth: Int,
    val mp4Url: String,
    val nsfw: Boolean,
    val numFrames: Int,
    val posterUrl: String,
    val published: Int,
    val ratio: Int,
    val source: Int,
    val tags: List<Any>,
    val thumb100PosterUrl: String,
    val type: Int,
    val userData: UserData,
    val userName: String,
    val views: Int,
    val views5: Int,
    val width: Int
)