package me.cniekirk.flex.ui.search.mvi

import androidx.annotation.StringRes

sealed class SearchSideEffect {
    data class Error(@StringRes val message: Int) : SearchSideEffect()
    data class RandomSelected(val subreddit: String) : SearchSideEffect()
}