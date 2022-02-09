package me.cniekirk.flex.data.remote.model.wikipedia


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Desktop(
    val edit: String?,
    val page: String?,
    val revisions: String?,
    val talk: String?
)