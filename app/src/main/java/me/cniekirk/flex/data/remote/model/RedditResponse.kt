package me.cniekirk.flex.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RedditResponse<T>(val kind: String, val data: T)
