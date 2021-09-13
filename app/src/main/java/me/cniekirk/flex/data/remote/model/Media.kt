package me.cniekirk.flex.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Media(@Json(name = "reddit_video") val redditVideo: RedditVideo?)
