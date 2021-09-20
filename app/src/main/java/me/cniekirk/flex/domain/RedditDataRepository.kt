package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.Data
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.data.remote.model.base.Listing
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedCommentData

interface RedditDataRepository {

    fun getComments(submissionId: String, sortType: String): Flow<RedditResult<List<Comment>>>

    fun getAccessToken(code: String): Flow<RedditResult<Token>>

    fun upvoteThing(thingId: String): Flow<RedditResult<Boolean>>

    fun removeVoteThing(thingId: String): Flow<RedditResult<Boolean>>

    fun downvoteThing(thingId: String): Flow<RedditResult<Boolean>>

}