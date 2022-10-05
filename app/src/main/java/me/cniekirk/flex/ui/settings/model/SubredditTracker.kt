package me.cniekirk.flex.ui.settings.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubredditTracker(
    @Json(name = "authorMatcher")
    val authorMatcher: String? = null,
    @Json(name = "flairMatcher")
    val flairMatcher: String? = null,
    @Json(name = "id")
    val id: Int? = null,
    @Json(name = "linkMatcher")
    val linkMatcher: String? = null,
    @Json(name = "minUpvoteMatcher")
    val minUpvoteMatcher: Int? = null,
    @Json(name = "name")
    val name: String? = null,
    @Json(name = "subredditName")
    val subredditName: String? = null,
    @Json(name = "titleMatcher")
    val titleMatcher: String? = null
)