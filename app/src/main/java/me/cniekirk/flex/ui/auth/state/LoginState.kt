package me.cniekirk.flex.ui.auth.state

import androidx.annotation.StringRes

data class LoginState(
    val placeholder: String = ""
)

sealed class LoginSideEffect {
    object LoginSuccess : LoginSideEffect()
    data class Error(@StringRes val message: Int): LoginSideEffect()
}