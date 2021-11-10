package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.rules.Rules
import me.cniekirk.flex.data.remote.model.subreddit.ModUser
import me.cniekirk.flex.data.remote.model.subreddit.Subreddit
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.GetSubredditInfoUseCase
import me.cniekirk.flex.domain.usecase.GetSubredditModeratorsUseCase
import me.cniekirk.flex.domain.usecase.GetSubredditRulesUseCase
import javax.inject.Inject

@HiltViewModel
class SubredditActionsViewModel @Inject constructor(
    private val getSubredditRulesUseCase: GetSubredditRulesUseCase,
    private val getSubredditInfoUseCase: GetSubredditInfoUseCase,
    private val getSubredditModeratorsUseCase: GetSubredditModeratorsUseCase
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

}