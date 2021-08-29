package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Preview(
    val enabled: Boolean,
    val images: List<Image>
)