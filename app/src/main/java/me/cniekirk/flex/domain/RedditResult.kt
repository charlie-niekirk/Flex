package me.cniekirk.flex.domain

sealed class RedditResult<out T> {
    data class Success<out T>(val data: T): RedditResult<T>()
    data class Error(val errorMessage: Throwable): RedditResult<Nothing>()
    object Loading : RedditResult<Nothing>()
    object UnAuthenticated : RedditResult<Nothing>()
}