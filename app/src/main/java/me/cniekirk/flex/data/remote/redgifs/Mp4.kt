package me.cniekirk.flex.data.remote.redgifs


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Mp4(
    val height: Int,
    val size: Int,
    val url: String,
    val width: Int
)