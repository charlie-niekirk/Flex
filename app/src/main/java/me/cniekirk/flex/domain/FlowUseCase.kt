package me.cniekirk.flex.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

abstract class FlowUseCase<in P, R: Any>(private val coroutineDispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(parameters: P): Flow<RedditResult<R>> =
        execute(parameters)
            .onStart { emit(RedditResult.Loading) }
            .retry(2) { e ->
                (e is Exception).also { if (it) delay(1000) }
            }
            .catch { throwable ->
                emit(RedditResult.Error(throwable))
            }.flowOn(coroutineDispatcher)

    abstract suspend fun execute(parameters: P): Flow<RedditResult<R>>
}