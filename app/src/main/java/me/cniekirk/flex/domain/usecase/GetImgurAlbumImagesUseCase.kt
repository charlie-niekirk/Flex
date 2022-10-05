package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.imgur.ImageData
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.ImgurDataRepository
import javax.inject.Inject

class GetImgurAlbumImagesUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val repository: ImgurDataRepository
) : FlowUseCase<String, ImgurResponse<List<ImageData>>>(coroutineDispatcher) {

    override suspend fun execute(parameters: String) =
        repository.getGalleryImages(parameters)
}