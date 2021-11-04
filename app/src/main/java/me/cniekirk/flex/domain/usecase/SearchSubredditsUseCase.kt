package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.Subreddit
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.model.SubredditSearchRequest
import javax.inject.Inject

class SearchSubredditsUseCase @Inject constructor(
    private val redditDataRepository: RedditDataRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<SubredditSearchRequest, List<Subreddit>>(coroutineDispatcher) {

    override suspend fun execute(parameters: SubredditSearchRequest) =
        redditDataRepository.searchSubreddits(parameters.query, parameters.sort)
}