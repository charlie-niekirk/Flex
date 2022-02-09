package me.cniekirk.flex.data.remote.model.reddit.listings

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.reddit.base.Listing
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentData

@JsonClass(generateAdapter = true)
@Parcelize
class CommentDataListing(
    @Json(name = "modhash")
    override val modhash: String?,
    @Json(name = "dist")
    override val dist: Int?,
    @Json(name = "children")
    override val children: List<EnvelopedCommentData>,
    @Json(name = "after")
    override val after: String?,
    @Json(name = "before")
    override val before: String?
) : Listing<EnvelopedCommentData>, Parcelable