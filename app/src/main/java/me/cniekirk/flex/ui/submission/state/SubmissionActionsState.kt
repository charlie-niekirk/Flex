package me.cniekirk.flex.ui.submission.state

import androidx.annotation.StringRes

data class SubmissionActionsState(
    val voteState: VoteState = VoteState.NoVote
)

sealed class SubmissionActionsEffect {
    data class ActionCompleted(@StringRes val message: Int) : SubmissionActionsEffect()
    data class Error(@StringRes val message: Int) : SubmissionActionsEffect()
    object SubmissionReminderSet : SubmissionActionsEffect()
}