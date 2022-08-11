package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import me.cniekirk.flex.R
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.GetMeUseCase
import me.cniekirk.flex.domain.usecase.GetSelfPostsUseCase
import me.cniekirk.flex.ui.auth.state.AccountViewSideEffect
import me.cniekirk.flex.ui.auth.state.AccountViewState
import me.cniekirk.flex.util.condense
import me.cniekirk.flex.util.getElapsedTime
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getMeUseCase: GetMeUseCase,
    private val getSelfPostsUseCase: GetSelfPostsUseCase
): ContainerHost<AccountViewState, AccountViewSideEffect>, ViewModel() {

    override val container = container<AccountViewState, AccountViewSideEffect>(
        AccountViewState()
    ) {
        loadAccount()
    }

    private fun loadAccount() = intent {
        getMeUseCase(null).collect {
            when (it) {
                is RedditResult.Error -> {
                    postSideEffect(AccountViewSideEffect.Toast(R.string.unknown_error))
                }
                RedditResult.Loading -> {
                    reduce { state.copy(loading = true) }
                }
                is RedditResult.Success -> {
                    reduce {
                        state.copy(
                            loading = false,
                            username = it.data.name ?: "",
                            postKarma = it.data.postKarma?.condense() ?: "",
                            commentKarma = it.data.commentKarma?.condense() ?: "",
                            accountAge = it.data.created?.getElapsedTime() ?: ""
                        )
                    }
                    loadPosts(it.data.name!!)
                }
                RedditResult.UnAuthenticated -> {
                    postSideEffect(AccountViewSideEffect.Toast(R.string.action_error_aunauthenticated))
                }
            }
        }
    }

    private fun loadPosts(username: String) = intent {
        getSelfPostsUseCase(username).cachedIn(viewModelScope).collect {
            reduce {
                state.copy(postsList = it)
            }
        }
    }
}