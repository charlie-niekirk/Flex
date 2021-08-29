package me.cniekirk.flex.domain

sealed class LoginState {
    object UnAuthenticated : LoginState()
    object Authenticated : LoginState()
}
