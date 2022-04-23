package me.cniekirk.flex.ui.state

import androidx.annotation.StringRes

sealed class AccountViewState {
    data class HeaderState(
        val username: String = "",
        val postKarma: String = "",
        val commentKarma: String = "",
        val accountAge: String = "",
        val loading: Boolean = false
    ): AccountViewState()
}

sealed class AccountViewSideEffect {
    data class Toast(@StringRes val message: Int): AccountViewSideEffect()
}