package me.cniekirk.flex.domain.model

import me.cniekirk.flex.data.remote.model.reddit.MoreComments

data class MoreCommentsRequest(val moreComments: MoreComments, val parentId: String)