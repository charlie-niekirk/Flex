package me.cniekirk.flex.data.remote.model.reddit.auth

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class RedditUser(
    val name: String?,
    val created: Long?,
    @Json(name = "comment_karma")
    val commentKarma: Long?,
    @Json(name = "link_karma")
    val postKarma: Long?
): Parcelable
