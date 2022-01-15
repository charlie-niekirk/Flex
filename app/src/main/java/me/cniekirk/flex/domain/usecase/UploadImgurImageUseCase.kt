package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import me.cniekirk.flex.data.remote.model.imgur.Data
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.ImgurDataRepository
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadImgurImageUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val imgurDataRepository: ImgurDataRepository
) : FlowUseCase<MultipartBody.Part, ImgurResponse<Data>>(coroutineDispatcher) {

    override suspend fun execute(parameters: MultipartBody.Part) =
        imgurDataRepository.uploadImage(parameters)
}