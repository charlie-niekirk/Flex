package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import me.cniekirk.flex.R
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.GetSubredditInfoUseCase
import me.cniekirk.flex.domain.usecase.SearchSubredditsUseCase
import me.cniekirk.flex.ui.search.mvi.SearchSideEffect
import me.cniekirk.flex.ui.search.mvi.SearchState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSubredditsUseCase: SearchSubredditsUseCase,
    private val getSubredditInfoUseCase: GetSubredditInfoUseCase
) : ViewModel(), ContainerHost<SearchState, SearchSideEffect> {

    override val container = container<SearchState, SearchSideEffect>(SearchState())

    fun randomClicked(randomType: String) = intent {
        getSubredditInfoUseCase(randomType).collect { result ->
            when (result) {
                is RedditResult.Error -> {
                    postSideEffect(SearchSideEffect.Error(R.string.generic_network_error))
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    result.data.displayName?.let { name ->
                        postSideEffect(SearchSideEffect.RandomSelected(name))
                    } ?: run {
                        postSideEffect(SearchSideEffect.Error(R.string.generic_network_error))
                    }
                }
            }
        }
    }

    fun searchSubreddit(query: String) {

    }
}