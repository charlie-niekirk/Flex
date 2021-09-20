package me.cniekirk.flex.data.remote.repo

import androidx.annotation.IntRange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.local.db.User
import me.cniekirk.flex.data.local.db.UserDao
import me.cniekirk.flex.data.local.prefs.Preferences
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
import javax.inject.Named

class RedditDataRepositoryImpl @Inject constructor(
    @Named("userlessApi") private val redditApi: RedditApi,
    @Named("authApi") private val authRedditApi: RedditApi,
    private val userDao: UserDao
) : RedditDataRepository {

    override fun getAccessToken(code: String): Flow<RedditResult<Token>> = flow {
        val response = redditApi.getAccessToken(getHttpBasicAuthHeader(), code.toAuthParams())
        userDao.insert(User(response.accessToken, response.refreshToken!!))
        emit(RedditResult.Success(response))
    }

    override fun upvoteThing(thingId: String) = vote(thingId, 1)

    override fun removeVoteThing(thingId: String) = vote(thingId, 0)

    override fun downvoteThing(thingId: String) = vote(thingId, -1)

    private fun vote(thingId: String, @IntRange(from = -1, to = 1) direction: Int): Flow<RedditResult<Boolean>> = flow {
        userDao.getAll().firstOrNull()?.let {
            authRedditApi.vote("Bearer ${it.accessToken}", thingId, direction)
            emit(RedditResult.Success(true))
        } ?: run {
            emit(RedditResult.UnAuthenticated)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getComments(submissionId: String, sortType: String): Flow<RedditResult<List<Comment>>> = flow {
        val response = if (userDao.getAll().isNullOrEmpty()) {
            redditApi.getCommentsForListing(submissionId, sortType)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            authRedditApi.getCommentsForListing(submissionId, sortType, accessToken)
        }
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