package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlexDataRepository
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.settings.model.SubredditTracker
import javax.inject.Inject

class AddTrackerUseCase @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher,
    private val redditDataRepository: RedditDataRepository
) : FlowUseCase<String, AuthedSubmission>(coroutineDispatcher) {

    override suspend fun execute(parameters: String) =
        redditDataRepository.getPostInfo(parameters)
}