package me.cniekirk.flex.domain.model

/**
 * Represents a query for subreddits
 */
data class SubredditSearchRequest(
    val query: String,
    val nsfw: Boolean = false,
    val sort: String = "relevance",
)
