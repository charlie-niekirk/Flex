package me.cniekirk.flex.data.remote.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.remote.ImgurApi
import me.cniekirk.flex.data.remote.model.imgur.Data
import me.cniekirk.flex.data.remote.model.imgur.ImageData
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import me.cniekirk.flex.domain.ImgurDataRepository
import me.cniekirk.flex.domain.RedditResult
import okhttp3.MultipartBody
import javax.inject.Inject

class ImgurDataRepositoryImpl @Inject constructor(
    private val imgurApi: ImgurApi
) : ImgurDataRepository {

    override fun uploadImage(image: MultipartBody.Part): Flow<RedditResult<ImgurResponse<Data>>> = flow {
        val response = imgurApi.uploadImage(image)
        emit(RedditResult.Success(response))
    }

    override fun getGalleryImages(albumHash: String): Flow<RedditResult<ImgurResponse<List<ImageData>>>> = flow {
        val response = imgurApi.getGalleryImages(albumHash)
        emit(RedditResult.Success(response))
    }

}