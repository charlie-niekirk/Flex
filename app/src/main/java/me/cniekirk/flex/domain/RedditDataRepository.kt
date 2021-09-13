package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.T3
import me.cniekirk.flex.data.remote.model.auth.Token

interface RedditDataRepository {

    fun getFrontpagePosts(sort: String): Flow<RedditResult<List<T3>>>

    fun getSubredditPosts(subreddit: String, sortType: String = "top"): Flow<RedditResult<List<T3>>>

    fun getAccessToken(code: String): Flow<RedditResult<Token>>

}