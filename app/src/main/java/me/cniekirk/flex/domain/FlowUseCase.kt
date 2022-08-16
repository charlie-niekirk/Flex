package me.cniekirk.flex.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import me.cniekirk.flex.data.map
import timber.log.Timber
import java.lang.Exception

abstract class FlowUseCase<in P, R: Any>(private val coroutineDispatcher: CoroutineDispatcher) {

    suspend operator fun invoke(parameters: P): Flow<RedditResult<R>> =
        execute(parameters)
            .onStart { emit(RedditResult.Loading) }
            .retry(2) { e ->
                (e is Exception).also { if (it) delay(1000) }
            }
            .catch { throwable ->
                Timber.e(throwable)
                emit(RedditResult.Error(throwable.map()))
            }.flowOn(coroutineDispatcher)

    abstract suspend fun execute(parameters: P): Flow<RedditResult<R>>
}