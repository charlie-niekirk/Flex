package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GalleryData(
    val items: List<Item>
)