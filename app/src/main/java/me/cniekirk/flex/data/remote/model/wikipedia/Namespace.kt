package me.cniekirk.flex.data.remote.model.wikipedia


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Namespace(
    val id: Int?,
    val text: String?
)