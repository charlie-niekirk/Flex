package me.cniekirk.flex.data.remote.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.local.Preferences
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.model.T3
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.util.getHttpBasicAuthHeader
import me.cniekirk.flex.util.toAuthParams
import javax.inject.Inject

class RedditDataRepositoryImpl @Inject constructor(
    private val redditApi: RedditApi,
    private val preferences: Preferences) : RedditDataRepository {

    override fun getFrontpagePosts(sort: String): Flow<RedditResult<List<T3>>> = flow {
        emit(RedditResult.Success(redditApi.getFrontpagePosts(sort).data.children.map { it.data }))
    }

    override fun getSubredditPosts(subreddit: String): Flow<RedditResult<List<T3>>> = flow {
        emit(RedditResult.Success(redditApi.getTopPosts(subreddit).data.children.map { it.data }))
    }

    override fun getAccessToken(code: String): Flow<RedditResult<Token>> = flow {
        val response = redditApi.getAccessToken(getHttpBasicAuthHeader(), code.toAuthParams())
        preferences.setAccessToken(response.accessToken)
        emit(RedditResult.Success(response))
    }
}