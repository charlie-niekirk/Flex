package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.auth.Token
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.LoginUseCase
import me.cniekirk.flex.ui.auth.state.LoginSideEffect
import me.cniekirk.flex.ui.auth.state.LoginState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container = container<LoginState, LoginSideEffect>(LoginState())

    fun onCodeIntercept(code: String) = intent {
        loginUseCase(code).collect { result ->
            when (result) {
                is RedditResult.Error -> {
                    postSideEffect(LoginSideEffect.Error(R.string.generic_network_error))
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    postSideEffect(LoginSideEffect.LoginSuccess)
                }
            }
        }
    }
}