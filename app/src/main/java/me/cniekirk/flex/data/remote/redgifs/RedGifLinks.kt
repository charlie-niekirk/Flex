package me.cniekirk.flex.data.remote.redgifs


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RedGifLinks(
    val gfyItem: GfyItem
)