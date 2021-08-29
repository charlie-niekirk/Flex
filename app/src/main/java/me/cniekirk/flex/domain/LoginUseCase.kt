package me.cniekirk.flex.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.di.IoDispatcher
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: RedditDataRepository,
    @IoDispatcher dispatcher: CoroutineDispatcher
) : FlowUseCase<String, Token>(dispatcher) {

    override suspend fun execute(parameters: String): Flow<RedditResult<Token>> =
        repository.getAccessToken(parameters)

}