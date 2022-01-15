package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.reddit.auth.Token
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: RedditDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<String, Token>(dispatcher) {

    override suspend fun execute(parameters: String): Flow<RedditResult<Token>> =
        repository.getAccessToken(parameters)

}