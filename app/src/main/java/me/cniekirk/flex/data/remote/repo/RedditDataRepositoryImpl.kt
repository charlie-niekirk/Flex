package me.cniekirk.flex.data.remote.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.local.Preferences
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.data.remote.model.MoreComments
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.data.remote.model.base.Listing
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedCommentData
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.util.getHttpBasicAuthHeader
import me.cniekirk.flex.util.toAuthParams
import javax.inject.Inject

class RedditDataRepositoryImpl @Inject constructor(
    private val redditApi: RedditApi,
    private val preferences: Preferences) : RedditDataRepository {

    override fun getFrontpagePosts(sort: String): Flow<RedditResult<List<Submission>>> = flow {
        emit(RedditResult.Success(redditApi.getFrontpagePosts(sort).data.children.map { it.data as Submission }))
    }

    override fun getSubredditPosts(subreddit: String, sortType: String): Flow<RedditResult<List<Submission>>> = flow {
        emit(RedditResult.Success(redditApi.getPosts(subreddit, sortType).data.children.map { it.data as Submission }))
    }

    override fun getAccessToken(code: String): Flow<RedditResult<Token>> = flow {
        val response = redditApi.getAccessToken(getHttpBasicAuthHeader(), code.toAuthParams())
        preferences.setAccessToken(response.accessToken)
        emit(RedditResult.Success(response))
    }

    @Suppress("UNCHECKED_CAST")
    override fun getComments(submissionId: String, sortType: String): Flow<RedditResult<List<Comment>>> = flow {
        val response = redditApi.getCommentsForListing(submissionId, sortType)
        val commentTree = response.lastOrNull()?.data as Listing<EnvelopedCommentData>
        val comments = mutableListOf<Comment>()
        buildTree(commentTree, comments)
        emit(RedditResult.Success(comments))
    }

    private fun buildTree(data: Listing<EnvelopedCommentData>, output: MutableList<Comment>) {
        data.children.forEach {
            val topLevel = it.data
            if (topLevel !is MoreComments) {
                output.add(topLevel as Comment)
                if (!topLevel.replies.isNullOrEmpty()) {
                    buildTree(topLevel.repliesRaw!!.data, output)
                }
            }
        }
    }
}