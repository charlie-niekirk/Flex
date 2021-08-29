package me.cniekirk.flex.domain

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.T3
import me.cniekirk.flex.di.IoDispatcher
import javax.inject.Inject

class GetFrontPageUseCase @Inject constructor(
    private val repository: RedditDataRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<String, List<T3>>(coroutineDispatcher) {

    override suspend fun execute(parameters: String) = repository.getFrontpagePosts(parameters)

}