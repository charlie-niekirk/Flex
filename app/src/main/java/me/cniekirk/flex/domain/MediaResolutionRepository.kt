package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.reddit.redgifs.GfycatLinks

interface MediaResolutionRepository {

    fun getRedGifLinks(gyfId: String): Flow<RedditResult<GfycatLinks>>

}