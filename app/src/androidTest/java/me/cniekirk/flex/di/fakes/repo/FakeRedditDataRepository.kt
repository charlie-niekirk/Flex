package me.cniekirk.flex.di.fakes.repo

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.pushshift.DeletedComment
import me.cniekirk.flex.data.remote.model.reddit.*
import me.cniekirk.flex.data.remote.model.reddit.auth.RedditUser
import me.cniekirk.flex.data.remote.model.reddit.auth.Token
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedContributionListing
import me.cniekirk.flex.data.remote.model.reddit.flair.UserFlairItem
import me.cniekirk.flex.data.remote.model.reddit.rules.Rules
import me.cniekirk.flex.data.remote.model.reddit.subreddit.ModUser
import me.cniekirk.flex.data.remote.model.reddit.subreddit.Subreddit
import me.cniekirk.flex.data.remote.model.wikipedia.WikiSummary
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.gallery.DownloadState
import javax.inject.Inject

class FakeRedditDataRepository @Inject constructor() : RedditDataRepository {

    override suspend fun getComments(
        submissionId: String,
        sortType: String
    ): List<EnvelopedContributionListing> {
        return listOf(

        )
    }

    override fun getMoreComments(
        moreComments: MoreComments,
        parentId: String
    ): Flow<RedditResult<List<CommentData>>> {
        TODO("Not yet implemented")
    }

    override fun getDeletedComment(commentId: String): Flow<RedditResult<DeletedComment>> {
        return flowOf()
    }

    override fun getAccessToken(code: String): Flow<RedditResult<Token>> {
        return flowOf()
    }

    override fun upvoteThing(thingId: String): Flow<RedditResult<Boolean>> {
        return flowOf()
    }

    override fun removeVoteThing(thingId: String): Flow<RedditResult<Boolean>> {
        return flowOf()
    }

    override fun downvoteThing(thingId: String): Flow<RedditResult<Boolean>> {
        return flowOf()
    }

    override fun searchSubreddits(
        query: String,
        sortType: String
    ): Flow<RedditResult<List<Subreddit>>> {
        return flowOf()
    }

    override fun getSubredditRules(subreddit: String): Flow<RedditResult<Rules>> {
        return flowOf()
    }

    override fun getSubredditInfo(subreddit: String): Flow<RedditResult<Subreddit>> {
        return flowOf()
    }

    override fun getPostInfo(postId: String): Flow<RedditResult<AuthedSubmission>> {
        return flowOf()
    }

    override fun getSubredditModerators(subreddit: String): Flow<RedditResult<List<ModUser>>> {
        return flowOf()
    }

    override fun subscribeSubreddit(subredditId: String): Flow<RedditResult<Int>> {
        return flowOf()
    }

    override fun unsubscribeSubreddit(subredditId: String): Flow<RedditResult<Int>> {
        return flowOf()
    }

    override fun favoriteSubreddit(subreddit: String): Flow<RedditResult<Int>> {
        return flowOf()
    }

    override fun unfavoriteSubreddit(subreddit: String): Flow<RedditResult<Int>> {
        return flowOf()
    }

    override fun getAvailableUserFlairs(subreddit: String): Flow<RedditResult<List<UserFlairItem>>> {
        return flowOf()
    }

    override fun submitComment(
        markdown: String,
        parentThing: String
    ): Flow<RedditResult<CommentData>> {
        return flowOf()
    }

    override fun downloadMedia(url: String): Flow<RedditResult<DownloadState>> {
        return flowOf()
    }

    override fun getMe(): Flow<RedditResult<RedditUser>> {
        return flowOf()
    }

    override fun getSelfPosts(username: String): Flow<PagingData<AuthedSubmission>> {
        return flowOf()
    }

    override suspend fun getWikipediaSummary(article: String): RedditResult<WikiSummary> {
        return RedditResult.Loading
    }

    companion object {
        const val COMMENT = ""
    }
}