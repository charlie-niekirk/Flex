package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.reddit.flair.UserFlairItem
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import javax.inject.Inject

class GetAvailableUserFlairUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val redditDataRepository: RedditDataRepository
) : FlowUseCase<String, List<UserFlairItem>>(coroutineDispatcher) {

    override suspend fun execute(parameters: String) =
        redditDataRepository.getAvailableUserFlairs(parameters)

}