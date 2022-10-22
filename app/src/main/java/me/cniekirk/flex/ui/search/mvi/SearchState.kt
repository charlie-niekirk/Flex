package me.cniekirk.flex.ui.search.mvi

import me.cniekirk.flex.data.remote.model.reddit.subreddit.Subreddit

data class SearchState(
    val searchResults: List<Subreddit> = emptyList()
)