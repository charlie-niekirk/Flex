package me.cniekirk.flex.data.remote.model.reddit.redgifs


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GfycatLinks(
    val gfyItem: GfyItem
)