package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import me.cniekirk.flex.data.local.db.UserDao
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.pagination.SubredditSubmissionsPagingSource
import me.cniekirk.flex.ui.submission.SubmissionListEvent
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SubmissionListViewModel @Inject constructor(
    @Named("userlessApi") private val redditApi: RedditApi,
    @Named("authApi") private val authRedditApi: RedditApi,
    private val userDao: UserDao
) : ViewModel() {

    private val _subredditFlow = MutableStateFlow(value = "tommyinnit")
    val subredditFlow = _subredditFlow.asStateFlow()
    private val _sortFlow = MutableStateFlow(value = "")
    val sortFlow = _sortFlow.asStateFlow()

    @ExperimentalCoroutinesApi
    val pagingSubmissionFlow = subredditFlow.flatMapLatest { subreddit ->
        sortFlow.flatMapLatest { sort ->
            Pager(config = PagingConfig(pageSize = 15, prefetchDistance = 1)) {
                SubredditSubmissionsPagingSource(redditApi, authRedditApi, subreddit, sort, userDao)
            }.flow.cachedIn(viewModelScope)
        }
    }

    fun onUiEvent(submissionListEvent: SubmissionListEvent) {
        when (submissionListEvent) {
            is SubmissionListEvent.SortUpdated -> {
                _sortFlow.value = "/${submissionListEvent.sort}"
            }
            is SubmissionListEvent.SubredditUpdated -> {
                _subredditFlow.value = submissionListEvent.subreddit
            }
        }
    }
}