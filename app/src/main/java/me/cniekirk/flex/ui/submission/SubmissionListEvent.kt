package me.cniekirk.flex.ui.submission

/**
 * The possible events coming form the SubmissionListFragment
 */
sealed class SubmissionListEvent {
    data class SortUpdated(val sort: String) : SubmissionListEvent()
    data class SubredditUpdated(val subreddit: String) : SubmissionListEvent()
    object SubredditOptions : SubmissionListEvent()
    data class RandomSubredditSelected(val randomType: String) : SubmissionListEvent()
}
