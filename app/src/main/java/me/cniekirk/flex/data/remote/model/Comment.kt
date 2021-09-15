package me.cniekirk.flex.data.remote.model

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import me.cniekirk.flex.data.remote.model.base.Contribution
import me.cniekirk.flex.data.remote.model.base.Listing
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedCommentData
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedCommentDataListing

@JsonClass(generateAdapter = true)
@Parcelize
data class Comment(
    @Json(name = "id")
    override val id: String,
    @Json(name = "name")
    override val fullname: String,
    @Json(name = "all_awardings")
    val allAwarding: List<AllAwarding>?,
    @Json(name = "author")
    val author: String,
    @Json(name = "body")
    val body: String,
    @Json(name = "body_html")
    val bodyHtml: String,
    @Json(name = "can_gild")
    val canGild: Boolean,
    @Json(name = "created")
    val created: Long,
    @Json(name = "created_utc")
    val createdUtc: Long,
    @Json(name = "edited")
    val editedRaw: @RawValue Any,
    @Json(name = "depth")
    override val depth: Int = 0,
    @Json(name = "distinguished")
    val distinguishedRaw: String?,
    @Json(name = "archived")
    val isArchived: Boolean,
    @Json(name = "locked")
    val isLocked: Boolean,
    @Json(name = "saved")
    val isSaved: Boolean,
    @Json(name = "score_hidden")
    val isScoreHidden: Boolean,
    @Json(name = "stickied")
    val isStickied: Boolean,
    @Json(name = "is_submitter")
    val isSubmitter: Boolean,
    val likes: Boolean?,
    @Json(name = "link_title")
    val linkTitle: String?,
    @Json(name = "link_author")
    val linkAuthor: String?,
    @Json(name = "link_id")
    val linkId: String?,
    @Json(name = "link_url")
    val linkUrl: String?,
    @Json(name = "link_permalink")
    val linkPermalink: String?,
    val gildings: Gildings,
    @Json(name = "parent_id")
    val parentId: String,
    val permalink: String,
    @Json(name = "replies")
    val repliesRaw: EnvelopedCommentDataListing?,
    @Transient
    override var replies: List<CommentData>? =
        repliesRaw?.data?.children?.map { it.data }?.toList(),
    @Transient
    override val parentFullname: String = parentId,
    val score: Int,
    val subreddit: String,
    @Json(name = "subreddit_id")
    val subredditId: String,
    @Json(name = "subreddit_name_prefixed")
    val subredditNamePrefixed: String

) : CommentData, Parcelable {

    override val hasReplies: Boolean
        get() {
            return replies != null && replies!!.isNotEmpty()
        }

    override val repliesSize: Int
        get() {
            return replies?.size ?: 0
        }
}