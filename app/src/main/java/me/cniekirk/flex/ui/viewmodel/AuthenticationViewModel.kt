package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.reddit.auth.Token
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.LoginUseCase
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase) : ViewModel() {

    private val _loginState: MutableStateFlow<RedditResult<Token>>
        = MutableStateFlow(RedditResult.Loading)
    val loginState: StateFlow<RedditResult<Token>> = _loginState

    fun onCodeIntercepted(code: String) {
        viewModelScope.launch {
            loginUseCase(code).collect { result -> _loginState.value = result }
        }
    }
}