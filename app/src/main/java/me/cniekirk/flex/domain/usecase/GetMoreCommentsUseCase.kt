package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.MoreCommentsRequest
import javax.inject.Inject

interface GetMoreCommentsUseCase {
    operator fun invoke(moreCommentsRequest: MoreCommentsRequest): Flow<RedditResult<List<CommentData>>>
}

class GetMoreCommentsUseCaseImpl @Inject constructor(
    private val repository: RedditDataRepository
) : GetMoreCommentsUseCase {
    override fun invoke(moreCommentsRequest: MoreCommentsRequest)
        = repository.getMoreComments(moreCommentsRequest.moreComments, moreCommentsRequest.parentId)

}