package me.cniekirk.flex.domain

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.di.IoDispatcher
import javax.inject.Inject

class GetFrontPageUseCase @Inject constructor(
    private val repository: RedditDataRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, List<Submission>>(coroutineDispatcher) {

    override suspend fun execute(parameters: String) = repository.getFrontpagePosts(parameters)

}