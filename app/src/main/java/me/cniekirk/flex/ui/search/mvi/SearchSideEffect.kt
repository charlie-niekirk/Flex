package me.cniekirk.flex.ui.search.mvi

import androidx.annotation.StringRes

sealed class SearchSideEffect {
    data class Error(@StringRes val message: Int) : SearchSideEffect()
    data class SubredditSelected(val subreddit: String) : SearchSideEffect()
}