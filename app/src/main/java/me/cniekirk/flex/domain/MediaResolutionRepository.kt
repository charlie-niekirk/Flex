package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.redgifs.RedGifLinks

interface MediaResolutionRepository {

    fun getRedGifLinks(gyfId: String): Flow<RedditResult<RedGifLinks>>

}