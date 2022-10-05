package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.reddit.auth.RedditUser
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val redditDataRepository: RedditDataRepository,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
): FlowUseCase<Any?, RedditUser>(coroutineDispatcher) {

    override suspend fun execute(parameters: Any?)
        = redditDataRepository.getMe()
}