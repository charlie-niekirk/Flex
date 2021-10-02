package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.ui.gallery.DownloadState
import javax.inject.Inject

class DownloadMediaUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val redditDataRepository: RedditDataRepository
) : FlowUseCase<String, DownloadState>(coroutineDispatcher) {

    override suspend fun execute(parameters: String) =
        redditDataRepository.downloadMedia(parameters)

}