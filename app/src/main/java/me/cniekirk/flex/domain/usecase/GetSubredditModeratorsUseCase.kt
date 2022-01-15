package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.reddit.subreddit.ModUser
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import javax.inject.Inject

class GetSubredditModeratorsUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val redditDataRepository: RedditDataRepository
) : FlowUseCase<String, List<ModUser>>(coroutineDispatcher) {

    override suspend fun execute(parameters: String) =
        redditDataRepository.getSubredditModerators(parameters)
}