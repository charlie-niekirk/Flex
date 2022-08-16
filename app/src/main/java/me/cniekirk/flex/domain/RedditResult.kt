package me.cniekirk.flex.domain

import me.cniekirk.flex.data.Cause

sealed class RedditResult<out S> {
    data class Success<out S>(val data: S): RedditResult<S>()
    data class Error(val cause: Cause): RedditResult<Nothing>()
    object Loading : RedditResult<Nothing>()
}