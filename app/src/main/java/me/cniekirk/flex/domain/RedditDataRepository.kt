package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.Data
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.data.remote.model.base.Listing
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedCommentData

interface RedditDataRepository {

    fun getFrontpagePosts(sort: String): Flow<RedditResult<List<Submission>>>

    fun getSubredditPosts(subreddit: String, sortType: String = "top"): Flow<RedditResult<List<Submission>>>

    fun getComments(submissionId: String, sortType: String): Flow<RedditResult<List<Comment>>>

    fun getAccessToken(code: String): Flow<RedditResult<Token>>

}