package me.cniekirk.flex.ui.submission.model

import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Resolution
import me.cniekirk.flex.ui.util.getElapsedTime

data class UiSubmission(
    val title: String,
    val author: String,
    val upVotes: Int,
    val upVotePercentage: Int,
    val previewImage: List<Resolution>,
    val timeSincePost: String,
    val submissionId: String
)

fun AuthedSubmission.toUiSubmission(): UiSubmission {
    return UiSubmission(
        this.title,
        this.authorFullname ?: "unknown",
        this.ups ?: 0,
        this.upvoteRatio.toInt(),
        this.preview?.images?.get(0)?.resolutions ?: listOf(),
        this.createdUtc.toLong().getElapsedTime()
    )
}
