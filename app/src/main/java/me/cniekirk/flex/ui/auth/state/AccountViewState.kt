package me.cniekirk.flex.ui.auth.state

import androidx.annotation.StringRes
import androidx.paging.PagingData
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission

data class AccountViewState(
    val username: String = "",
    val postKarma: String = "",
    val commentKarma: String = "",
    val accountAge: String = "",
    val loading: Boolean = false,
    val postsList: PagingData<AuthedSubmission> = PagingData.empty(),
    val commentsList: PagingData<AuthedSubmission> = PagingData.empty()
)

sealed class AccountViewSideEffect {
    data class Toast(@StringRes val message: Int): AccountViewSideEffect()
}