package me.cniekirk.flex.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.di.IoDispatcher
import javax.inject.Inject

/**
 * Gets a Subreddit with all of it's posts
 */
class GetSubredditUseCase @Inject constructor(
    private val repository: RedditDataRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher)
    : FlowUseCase<String, List<Submission>>(coroutineDispatcher) {

    override suspend fun execute(parameters: String): Flow<RedditResult<List<Submission>>> =
        repository.getSubredditPosts(parameters)

}