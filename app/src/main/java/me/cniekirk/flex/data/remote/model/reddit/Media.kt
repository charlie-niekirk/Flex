package me.cniekirk.flex.data.remote.model.reddit

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Media(
    @Json(name = "reddit_video") val redditVideo: RedditVideo?,
    @Json(name = "oembed") val oembed: OEmbed?) : Parcelable
