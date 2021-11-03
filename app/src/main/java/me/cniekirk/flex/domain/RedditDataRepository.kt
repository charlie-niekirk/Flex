package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.*
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.data.remote.model.base.Listing
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedCommentData
import me.cniekirk.flex.ui.gallery.DownloadState

interface RedditDataRepository {

    fun getComments(submissionId: String, sortType: String): Flow<RedditResult<List<CommentData>>>

    fun getMoreComments(moreComments: MoreComments, parentId: String): Flow<RedditResult<List<CommentData>>>

    fun getAccessToken(code: String): Flow<RedditResult<Token>>

    fun upvoteThing(thingId: String): Flow<RedditResult<Boolean>>

    fun removeVoteThing(thingId: String): Flow<RedditResult<Boolean>>

    fun downvoteThing(thingId: String): Flow<RedditResult<Boolean>>

    fun downloadMedia(url: String): Flow<RedditResult<DownloadState>>

}