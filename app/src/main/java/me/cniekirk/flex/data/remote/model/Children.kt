package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Children(
    val data: T3,
    val kind: String
)