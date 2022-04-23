package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.cniekirk.flex.R
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.GetMeUseCase
import me.cniekirk.flex.ui.state.AccountViewSideEffect
import me.cniekirk.flex.ui.state.AccountViewState
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
    private val getMeUseCase: GetMeUseCase
): ContainerHost<AccountViewState, AccountViewSideEffect>, ViewModel() {

    override val container = container<AccountViewState, AccountViewSideEffect>(
        AccountViewState.HeaderState()
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
                    reduce { (state as AccountViewState.HeaderState).copy(loading = true) }
                }
                is RedditResult.Success -> {
                    reduce {
                        (state as AccountViewState.HeaderState).copy(
                            loading = false,
                            username = it.data.name ?: "",
                            postKarma = it.data.postKarma?.condense() ?: "",
                            commentKarma = it.data.commentKarma?.condense() ?: "",
                            accountAge = it.data.created?.getElapsedTime() ?: ""
                        )
                    }
                }
                RedditResult.UnAuthenticated -> {
                    postSideEffect(AccountViewSideEffect.Toast(R.string.action_error_aunauthenticated))
                }
            }
        }
    }
}