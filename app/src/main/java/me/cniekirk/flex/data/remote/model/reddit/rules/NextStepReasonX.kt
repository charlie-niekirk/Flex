package me.cniekirk.flex.data.remote.model.reddit.rules


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NextStepReasonX(
    val reasonText: String,
    val reasonTextToShow: String
)