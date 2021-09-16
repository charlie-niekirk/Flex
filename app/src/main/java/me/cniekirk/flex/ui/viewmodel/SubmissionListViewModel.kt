package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.pagination.SubredditSubmissionsPagingSource
import me.cniekirk.flex.domain.GetCommentsUseCase
import me.cniekirk.flex.ui.submission.SubmissionListEvent
import javax.inject.Inject

@HiltViewModel
class SubmissionListViewModel @Inject constructor(
    private val redditApi: RedditApi
) : ViewModel() {
    private val _subredditFlow = MutableStateFlow(value = "androidapps")
    val subredditFlow = _subredditFlow.asStateFlow()
    private val _sortFlow = MutableStateFlow(value = "")
    val sortFlow = _sortFlow.asStateFlow()

    @ExperimentalCoroutinesApi
    val pagingSubmissionFlow = subredditFlow.flatMapLatest { subreddit ->
        sortFlow.flatMapLatest { sort ->
            Pager(config = PagingConfig(pageSize = 15, prefetchDistance = 1)) {
                SubredditSubmissionsPagingSource(redditApi, subreddit, sort)
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