package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.dao.UserDao
import me.cniekirk.flex.data.local.prefs.Preferences
import me.cniekirk.flex.data.remote.GfycatApi
import me.cniekirk.flex.data.remote.RedGifsApi
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.StreamableApi
import me.cniekirk.flex.data.remote.model.subreddit.Subreddit
import me.cniekirk.flex.data.remote.pagination.SubredditSubmissionsPagingSource
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.SubredditSearchRequest
import me.cniekirk.flex.domain.usecase.GetSubredditInfoUseCase
import me.cniekirk.flex.domain.usecase.SearchSubredditsUseCase
import me.cniekirk.flex.ui.model.UserPreferences
import me.cniekirk.flex.ui.submission.SubmissionListEvent
import javax.inject.Inject
import javax.inject.Named

@ExperimentalCoroutinesApi
@HiltViewModel
class SubmissionListViewModel @Inject constructor(
    @Named("preLoginApi") private val redditApi: RedditApi,
    @Named("loginApi") private val authRedditApi: RedditApi,
    private val streamableApi: StreamableApi,
    private val gfycatApi: GfycatApi,
    private val redGifsApi: RedGifsApi,
    private val preferences: Preferences,
    private val preLoginUserDao: PreLoginUserDao,
    private val userDao: UserDao,
    private val imageLoader: ImageLoader,
    private val imageRequest: ImageRequest.Builder,
    private val searchSubredditsUseCase: SearchSubredditsUseCase,
    private val getSubredditInfoUseCase: GetSubredditInfoUseCase
) : ViewModel() {

    private val _subredditFlow = MutableStateFlow(value = "apple")
    val subredditFlow = _subredditFlow.asStateFlow()
    private val _sortFlow = MutableStateFlow(value = "")
    val sortFlow = _sortFlow.asStateFlow()
    private val _userPrefsFlow: MutableStateFlow<UserPreferences?> = MutableStateFlow(value = null)
    val userPrefsFlow = _userPrefsFlow.asStateFlow()
    private val _subredditInfo = MutableSharedFlow<RedditResult<Subreddit>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val subredditInfo: Flow<RedditResult<Subreddit>> = _subredditInfo.distinctUntilChanged()

    @ExperimentalCoroutinesApi
    val pagingSubmissionFlow = subredditFlow.flatMapLatest { subreddit ->
        sortFlow.flatMapLatest { sort ->
            Pager(config = PagingConfig(pageSize = 15, prefetchDistance = 5)) {
                SubredditSubmissionsPagingSource(redditApi, authRedditApi, streamableApi,
                    gfycatApi, redGifsApi, subreddit, sort, preLoginUserDao, userDao, imageRequest, imageLoader)
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
            SubmissionListEvent.SubredditOptions -> {
                viewModelScope.launch {
                    getSubredditInfoUseCase(subredditFlow.value).collect {
                        _subredditInfo.emit(it)
                    }
                }
            }
            is SubmissionListEvent.RandomSubredditSelected -> {
                viewModelScope.launch {
                    getSubredditInfoUseCase(submissionListEvent.randomType).collect {
                        when (it) {
                            is RedditResult.Success -> {
                                it.data.displayName?.let { name ->
                                    _subredditFlow.value = name
                                } ?: run {
                                    //TODO: Emit another error
                                }
                            }
                            is RedditResult.Error -> {
                                //TODO: Emit error
                            }
                            else -> {
                                //TODO: Emit generic error state
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun searchSubreddit(query: String): Flow<RedditResult<List<Subreddit>>> =
        searchSubredditsUseCase(SubredditSearchRequest(query))

    fun getPreferences() {
        viewModelScope.launch {
            preferences.blurNsfwFlow.collect { _userPrefsFlow.value = UserPreferences(it) }
        }
    }

    fun resetSubredditInfo() {
        _subredditInfo.resetReplayCache()
    }
}