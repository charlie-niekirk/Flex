package me.cniekirk.flex.data.remote.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.remote.ImgurApi
import me.cniekirk.flex.data.remote.imgur.ImgurResponse
import me.cniekirk.flex.domain.ImgurDataRepository
import me.cniekirk.flex.domain.RedditResult
import okhttp3.MultipartBody
import javax.inject.Inject

class ImgurDataRepositoryImpl @Inject constructor(
    private val imgurApi: ImgurApi
) : ImgurDataRepository {

    override fun uploadImage(image: MultipartBody.Part): Flow<RedditResult<ImgurResponse>> = flow {
        val response = imgurApi.uploadImage(image)
        emit(RedditResult.Success(response))
    }

}