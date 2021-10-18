package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.streamable.StreamableVideo
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.MediaResolutionRepository
import javax.inject.Inject

//class GetStreamableLinks @Inject constructor(
//    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
//    private val mediaResolutionRepository: MediaResolutionRepository
//) : FlowUseCase<String, StreamableVideo>(coroutineDispatcher) {
//
//    override suspend fun execute(parameters: String) =
//        mediaResolutionRepository.getStreamableLinks(parameters)
//
//}