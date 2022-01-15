package me.cniekirk.flex.data.remote.model.reddit.subreddit


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModUser(
    @Json(name = "author_flair_css_class")
    val authorFlairCssClass: Any?,
    @Json(name = "author_flair_text")
    val authorFlairText: String?,
    val date: Double?,
    val id: String?,
    @Json(name = "mod_permissions")
    val modPermissions: List<String>?,
    val name: String?,
    @Json(name = "rel_id")
    val relId: String?
)