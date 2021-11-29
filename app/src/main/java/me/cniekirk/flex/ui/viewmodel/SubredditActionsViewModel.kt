package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.flair.UserFlairItem
import me.cniekirk.flex.data.remote.model.rules.Rules
import me.cniekirk.flex.data.remote.model.subreddit.ModUser
import me.cniekirk.flex.data.remote.model.subreddit.Subreddit
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.*
import javax.inject.Inject

@HiltViewModel
class SubredditActionsViewModel @Inject constructor(
    private val getSubredditRulesUseCase: GetSubredditRulesUseCase,
    private val getSubredditInfoUseCase: GetSubredditInfoUseCase,
    private val getSubredditModeratorsUseCase: GetSubredditModeratorsUseCase,
    private val subscribeSubredditUseCase: SubscribeSubredditUseCase,
    private val unsubscribeSubredditUseCase: UnsubscribeSubredditUseCase,
    private val favoriteSubredditUseCase: FavoriteSubredditUseCase,
    private val unfavoriteSubredditUseCase: UnfavoriteSubredditUseCase,
    private val getAvailableUserFlairUseCase: GetAvailableUserFlairUseCase
) : ViewModel() {

    private val _rules = MutableSharedFlow<RedditResult<Rules>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val rules: Flow<RedditResult<Rules>> = _rules.distinctUntilChanged()

    private val _info = MutableSharedFlow<RedditResult<Subreddit>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val info: Flow<RedditResult<Subreddit>> = _info.distinctUntilChanged()

    private val _moderators = MutableSharedFlow<RedditResult<List<ModUser>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val moderators: Flow<RedditResult<List<ModUser>>> = _moderators.distinctUntilChanged()

    private val _singleActionState = MutableSharedFlow<RedditResult<Int>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val singleActionState: Flow<RedditResult<Int>> = _singleActionState.distinctUntilChanged()

    private val _userFlairs = MutableSharedFlow<RedditResult<List<UserFlairItem>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val userFlairs: Flow<RedditResult<List<UserFlairItem>>> = _userFlairs.distinctUntilChanged()

    fun getSubredditRules(subreddit: String) {
        viewModelScope.launch {
            getSubredditRulesUseCase(subreddit).collect { _rules.emit(it) }
        }
    }

    fun getSubredditInfo(subreddit: String) {
        viewModelScope.launch {
            getSubredditInfoUseCase(subreddit).collect { _info.emit(it) }
        }
    }

    fun getSubredditModerators(subreddit: String) {
        viewModelScope.launch {
            getSubredditModeratorsUseCase(subreddit).collect { _moderators.emit(it) }
        }
    }

    fun subscribeSubreddit(subreddit: String, isSubscribed: Boolean) {
        viewModelScope.launch {
            if (isSubscribed) {
                unsubscribeSubredditUseCase(subreddit).collect { _singleActionState.emit(it) }
            } else {
                subscribeSubredditUseCase(subreddit).collect { _singleActionState.emit(it) }
            }
        }
    }

    fun favoriteSubreddit(subreddit: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                unfavoriteSubredditUseCase(subreddit).collect { _singleActionState.emit(it) }
            } else {
                favoriteSubredditUseCase(subreddit).collect { _singleActionState.emit(it) }
            }
        }
    }

    fun getAvailableFlair(subreddit: String) {
        viewModelScope.launch {
            getAvailableUserFlairUseCase(subreddit).collect { _userFlairs.emit(it) }
        }
    }
}