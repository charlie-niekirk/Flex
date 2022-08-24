package me.cniekirk.flex.di.fakes.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.imgur.Data
import me.cniekirk.flex.data.remote.model.imgur.ImageData
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import me.cniekirk.flex.domain.ImgurDataRepository
import me.cniekirk.flex.domain.RedditResult
import okhttp3.MultipartBody
import javax.inject.Inject

class FakeImgurDataRepository @Inject constructor() : ImgurDataRepository {

    override fun uploadImage(image: MultipartBody.Part): Flow<RedditResult<ImgurResponse<Data>>> {
        return flowOf(RedditResult.Success(
            ImgurResponse(
                data = null,
                status = 1,
                success = true
            )
        ))
    }

    override fun getGalleryImages(albumHash: String): Flow<RedditResult<ImgurResponse<List<ImageData>>>> {
        return flowOf(RedditResult.Success(
            ImgurResponse(listOf(), 1, true)
        ))
    }
}
