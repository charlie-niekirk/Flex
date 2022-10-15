package me.cniekirk.flex.ui.submission.model

import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Resolution
import me.cniekirk.flex.ui.util.getElapsedTime
import me.cniekirk.flex.util.Link
import me.cniekirk.flex.util.processLink

data class UiSubmission(
    val title: String,
    val author: String,
    val selfText: String,
    val isSelf: Boolean,
    val linkType: Link?,
    val upVotes: Int,
    val upVotePercentage: Int,
    val numComments: String,
    val previewImage: List<Resolution>,
    val timeSincePost: String,
    val submissionId: String
)

fun AuthedSubmission.toUiSubmission(): UiSubmission {
    return UiSubmission(
        this.title,
        this.authorFullname ?: "unknown",
        this.selftext ?: "",
        this.isSelf ?: false,
        this.urlOverriddenByDest?.processLink(),
        this.ups ?: 0,
        this.upvoteRatio.toInt(),
        this.numComments?.toString() ?: "?",
        this.preview?.images?.get(0)?.resolutions ?: listOf(),
        this.createdUtc.toLong().getElapsedTime(),
        this.id
    )
}
