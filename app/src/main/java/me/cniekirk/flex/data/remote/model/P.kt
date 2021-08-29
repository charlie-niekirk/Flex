package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class P(
    val u: String,
    val x: Int,
    val y: Int
)