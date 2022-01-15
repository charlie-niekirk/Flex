package me.cniekirk.flex.data.remote.model.reddit.subreddit

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class PollOption(
    val text: String?,
    val id: String?
) : Parcelable
