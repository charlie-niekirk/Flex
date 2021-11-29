package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.CommentData
import me.cniekirk.flex.data.remote.model.MoreComments
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedContributionListing
import me.cniekirk.flex.data.remote.model.flair.UserFlairItem
import me.cniekirk.flex.data.remote.model.rules.Rules
import me.cniekirk.flex.data.remote.model.subreddit.ModUser
import me.cniekirk.flex.data.remote.model.subreddit.Subreddit
import me.cniekirk.flex.ui.gallery.DownloadState

interface RedditDataRepository {

    suspend fun getComments(submissionId: String, sortType: String): List<EnvelopedContributionListing>

    fun getMoreComments(moreComments: MoreComments, parentId: String): Flow<RedditResult<List<CommentData>>>

    fun getAccessToken(code: String): Flow<RedditResult<Token>>

    fun upvoteThing(thingId: String): Flow<RedditResult<Boolean>>

    fun removeVoteThing(thingId: String): Flow<RedditResult<Boolean>>

    fun downvoteThing(thingId: String): Flow<RedditResult<Boolean>>

    fun searchSubreddits(query: String, sortType: String): Flow<RedditResult<List<Subreddit>>>

    fun getSubredditRules(subreddit: String): Flow<RedditResult<Rules>>

    fun getSubredditInfo(subreddit: String): Flow<RedditResult<Subreddit>>

    fun getSubredditModerators(subreddit: String): Flow<RedditResult<List<ModUser>>>

    fun subscribeSubreddit(subredditId: String): Flow<RedditResult<Int>>

    fun unsubscribeSubreddit(subredditId: String): Flow<RedditResult<Int>>

    fun favoriteSubreddit(subreddit: String): Flow<RedditResult<Int>>

    fun unfavoriteSubreddit(subreddit: String): Flow<RedditResult<Int>>

    fun getAvailableUserFlairs(subreddit: String): Flow<RedditResult<List<UserFlairItem>>>

    fun submitComment(markdown: String, parentThing: String): Flow<RedditResult<CommentData>>

    fun downloadMedia(url: String): Flow<RedditResult<DownloadState>>
}