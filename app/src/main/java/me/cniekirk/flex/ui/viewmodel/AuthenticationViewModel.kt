package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.data.Cause
import me.cniekirk.flex.data.remote.model.reddit.auth.Token
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.GetMeUseCase
import me.cniekirk.flex.domain.usecase.LoginUseCase
import me.cniekirk.flex.ui.auth.state.LoginSideEffect
import me.cniekirk.flex.ui.auth.state.LoginState
import me.cniekirk.flex.ui.auth.state.Stage
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getMeUseCase: GetMeUseCase
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container = container<LoginState, LoginSideEffect>(LoginState()) {
        loadUserProfile()
    }

    fun onCodeIntercept(code: String) = intent {
        loginUseCase(code).collect { result ->
            when (result) {
                is RedditResult.Error -> {
                    postSideEffect(LoginSideEffect.Error(R.string.generic_network_error))
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    loadUserProfile()
                    postSideEffect(LoginSideEffect.LoginSuccess)
                }
            }
        }
    }

    private fun loadUserProfile() = intent {
        getMeUseCase(null).collect { result ->
            when (result) {
                is RedditResult.Error -> {
                    when (result.cause) {
                        Cause.Unauthenticated -> {
                            // Set to Pre login stage
                            reduce { state.copy(stage = Stage.PRE_LOGIN) }
                        }
                        Cause.InsufficientStorage, Cause.NetworkError, Cause.NoConnection,
                        Cause.NotFound, Cause.ServerError, Cause.Unknown -> {
                            postSideEffect(LoginSideEffect.Error(R.string.generic_network_error))
                        }
                    }
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    reduce { state.copy(redditUser = result.data, stage = Stage.POST_LOGIN) }
                }
            }
        }
    }
}