package me.cniekirk.flex.ui.submission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.pagination.SubredditSubmissionsPagingSource
import javax.inject.Inject

@HiltViewModel
class SubmissionListViewModel @Inject constructor(
    private val redditApi: RedditApi
) : ViewModel() {

    private val subredditFlow = MutableStateFlow(value = "tommyinnit")
    private val sortFlow = MutableStateFlow(value = "")

    @ExperimentalCoroutinesApi
    val pagingSubmissionFlow = subredditFlow.flatMapLatest { subreddit ->
        sortFlow.flatMapLatest { sort ->
            Pager(config = PagingConfig(pageSize = 15, prefetchDistance = 3)) {
                SubredditSubmissionsPagingSource(redditApi, subreddit, sort)
            }.flow.cachedIn(viewModelScope)
        }
    }

}