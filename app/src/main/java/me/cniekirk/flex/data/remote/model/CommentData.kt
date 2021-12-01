package me.cniekirk.flex.data.remote.model

import me.cniekirk.flex.data.remote.model.base.Contribution

interface CommentData : Contribution {
    val depth: Int
    val parentFullname: String?
    val hasReplies: Boolean
    val replies: List<CommentData>?
    val repliesSize: Int
    var isCollapsed: Boolean
}