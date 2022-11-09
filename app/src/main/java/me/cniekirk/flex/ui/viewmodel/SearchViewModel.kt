package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.SubredditSearchRequest
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
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchSubredditsUseCase: SearchSubredditsUseCase,
    private val getSubredditInfoUseCase: GetSubredditInfoUseCase
) : ViewModel(), ContainerHost<SearchState, SearchSideEffect> {

    // Internal flow to be collected so searches can be performed
    private val _searchFlow = MutableSharedFlow<String>()

    override val container = container<SearchState, SearchSideEffect>(SearchState())

    init {
        viewModelScope.launch {
            _searchFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    searchSubreddit(query)
                }
        }
    }

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

    /**
     * Just submits the query to the [_searchFlow]
     *
     * @param query the [String] query the UI has posted
     */
    fun onSearch(query: String) = intent { _searchFlow.emit(query) }

    private fun searchSubreddit(query: String) = intent {
        searchSubredditsUseCase(SubredditSearchRequest(query)).collect { result ->
            when (result) {
                is RedditResult.Error -> {
                    postSideEffect(SearchSideEffect.Error(R.string.generic_network_error))
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    Timber.d("Size: ${result.data.size}")
                    reduce { state.copy(searchResults = result.data) }
                }
            }
        }
    }
}