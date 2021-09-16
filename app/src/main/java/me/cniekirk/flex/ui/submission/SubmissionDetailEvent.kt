package me.cniekirk.flex.ui.submission

sealed class SubmissionDetailEvent {
    data class Upvote(val thingId: String) : SubmissionDetailEvent()
    data class RemoveUpvote(val thingId: String) : SubmissionDetailEvent()
    data class Downvote(val thingId: String) : SubmissionDetailEvent()
    data class RemoveDownvote(val thingId: String) : SubmissionDetailEvent()
}