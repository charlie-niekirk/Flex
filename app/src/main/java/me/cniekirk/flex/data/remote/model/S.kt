package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class S(
    val u: String,
    val x: Int,
    val y: Int
)