package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(
    private val repository: RedditDataRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<CommentRequest, List<Comment>>(coroutineDispatcher) {

    override suspend fun execute(parameters: CommentRequest): Flow<RedditResult<List<Comment>>> =
        repository.getComments(parameters.submissionId, parameters.sortType)

}