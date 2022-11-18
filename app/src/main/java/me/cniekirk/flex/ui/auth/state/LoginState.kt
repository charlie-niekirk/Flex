package me.cniekirk.flex.ui.auth.state

import androidx.annotation.StringRes
import me.cniekirk.flex.data.remote.model.reddit.auth.RedditUser

enum class Stage {
    PRE_LOGIN,
    POST_LOGIN
}

data class LoginState(
    val stage: Stage? = null,
    val redditUser: RedditUser = RedditUser(null, null, null, null)
)

sealed class LoginSideEffect {
    object LoginSuccess : LoginSideEffect()
    data class Error(@StringRes val message: Int): LoginSideEffect()
}