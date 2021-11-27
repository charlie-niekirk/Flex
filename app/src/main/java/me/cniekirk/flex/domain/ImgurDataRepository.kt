package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.imgur.ImgurResponse
import okhttp3.MultipartBody

interface ImgurDataRepository {

    fun uploadImage(image: MultipartBody.Part): Flow<RedditResult<ImgurResponse>>

}