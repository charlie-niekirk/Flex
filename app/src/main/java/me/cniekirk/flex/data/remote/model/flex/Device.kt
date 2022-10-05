package me.cniekirk.flex.data.remote.model.flex

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Device(
    val deviceToken: String
)
