package me.cniekirk.flex.data.remote.model.reddit

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import me.cniekirk.flex.util.ContentLink

@JsonClass(generateAdapter = true)
@Parcelize
data class MoreComments(
    @Json(name = "id")
    override val id: String,
    @Json(name = "name")
    override val fullname: String,
    @Json(name = "count")
    val count: Int,
    @Json(name = "depth")
    override val depth: Int,
    @Json(name = "parent_id")
    val parentId: String,
    @Json(name = "children")
    val children: List<String>,
    @Transient
    override val parentFullname: String = parentId,
    @Transient
    override val replies: List<CommentData>? = null,
    @Transient
    override val hasReplies: Boolean = false,
    @Transient
    override val repliesSize: Int = 0,
    @Transient
    override var isCollapsed: Boolean = false,
    @Transient
    override val contentLinks: @RawValue List<ContentLink>? = null
) : CommentData, Parcelable