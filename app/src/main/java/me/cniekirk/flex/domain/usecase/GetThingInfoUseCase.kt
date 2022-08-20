package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.domain.RedditResult

interface GetThingInfoUseCase {
    operator fun invoke(postId: String): Flow<RedditResult<AuthedSubmission>>
}