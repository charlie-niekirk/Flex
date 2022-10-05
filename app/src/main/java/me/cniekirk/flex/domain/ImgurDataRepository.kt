package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.imgur.Data
import me.cniekirk.flex.data.remote.model.imgur.ImageData
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import okhttp3.MultipartBody

interface ImgurDataRepository {

    fun uploadImage(image: MultipartBody.Part): Flow<RedditResult<ImgurResponse<Data>>>

    fun getGalleryImages(albumHash: String): Flow<RedditResult<ImgurResponse<List<ImageData>>>>
}