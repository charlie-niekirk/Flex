package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.CommentData
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.model.CommentParams
import javax.inject.Inject

class SubmitCommentUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val redditDataRepository: RedditDataRepository
) : FlowUseCase<CommentParams, CommentData>(coroutineDispatcher) {

    override suspend fun execute(parameters: CommentParams) =
        redditDataRepository.submitComment(parameters.markdown, parameters.parentThing)
}