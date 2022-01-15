package me.cniekirk.flex.data.remote.model.reddit

import me.cniekirk.flex.data.remote.model.reddit.base.Contribution
import me.cniekirk.flex.util.ContentLink

interface CommentData : Contribution {
    val depth: Int
    val parentFullname: String?
    val hasReplies: Boolean
    val replies: List<CommentData>?
    val repliesSize: Int
    var isCollapsed: Boolean
    val contentLinks: List<ContentLink>?
}