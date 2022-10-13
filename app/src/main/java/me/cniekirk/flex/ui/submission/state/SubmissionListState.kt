package me.cniekirk.flex.ui.submission.state

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission

data class SubmissionListState(
    val submissions: Flow<PagingData<AuthedSubmission>> = flowOf(),
    val subreddit: String = "apolloapp",
    val sort: String = "new"
)

sealed class SubmissionListSideEffect {
    object SubmissionReminderSet : SubmissionListSideEffect()
}