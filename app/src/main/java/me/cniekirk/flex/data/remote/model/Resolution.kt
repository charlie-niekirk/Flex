package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Resolution(
    val height: Int,
    val url: String,
    val width: Int
)