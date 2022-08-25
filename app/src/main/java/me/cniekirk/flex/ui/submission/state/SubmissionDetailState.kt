package me.cniekirk.flex.ui.submission.state

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.Cause
import me.cniekirk.flex.data.remote.model.reddit.CommentData

data class SubmissionDetailState(
    val comments: List<CommentData> = emptyList(),
    val voteState: VoteState = VoteState.NoVote
)

@Parcelize
sealed class VoteState : Parcelable {
    object Upvote : VoteState()
    object Downvote : VoteState()
    object NoVote : VoteState()
}

sealed class SubmissionDetailEffect {
    data class ShowError(@StringRes val message: Int) : SubmissionDetailEffect()
}