package me.cniekirk.flex.data.remote.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.remote.RedGifsApi
import me.cniekirk.flex.data.remote.redgifs.RedGifLinks
import me.cniekirk.flex.domain.MediaResolutionRepository
import me.cniekirk.flex.domain.RedditResult
import javax.inject.Inject

class MediaResolutionRepositoryImpl @Inject constructor(
    private val redGifsApi: RedGifsApi
) : MediaResolutionRepository {

    override fun getRedGifLinks(gyfId: String): Flow<RedditResult<RedGifLinks>> = flow {
        val response = redGifsApi.getDirectLinks(gyfId)
        emit(RedditResult.Success(response))
    }

}