package me.cniekirk.flex.ui.submission.state

import androidx.annotation.StringRes
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.ui.submission.model.UiSubmission

data class SubmissionListState(
    val submissions: Flow<PagingData<UiSubmission>> = flowOf(),
    val subreddit: String = "mechanicalkeyboards",
    val sort: String = "best"
)

sealed class SubmissionListSideEffect {
    object SubmissionReminderSet : SubmissionListSideEffect()
    data class Error(@StringRes val errorMessage: Int) : SubmissionListSideEffect()
}