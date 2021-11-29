package me.cniekirk.flex.data.remote.model.rules


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SiteRulesFlow(
    val nextStepHeader: String?,
    val nextStepReasons: List<NextStepReason>?,
    val reasonText: String?,
    val reasonTextToShow: String?
)