package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.CommentData
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.MoreCommentsRequest
import javax.inject.Inject

class GetMoreCommentsUseCase @Inject constructor(
    private val repository: RedditDataRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<MoreCommentsRequest, List<CommentData>>(coroutineDispatcher) {

    override suspend fun execute(parameters: MoreCommentsRequest): Flow<RedditResult<List<CommentData>>> =
        repository.getMoreComments(parameters.moreComments, parameters.parentId)

}