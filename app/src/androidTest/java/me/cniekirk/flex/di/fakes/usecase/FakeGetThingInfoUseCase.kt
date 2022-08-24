package me.cniekirk.flex.di.fakes.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.GetThingInfoUseCase
import javax.inject.Inject

class FakeGetThingInfoUseCase @Inject constructor() : GetThingInfoUseCase {

    override fun invoke(postId: String): Flow<RedditResult<AuthedSubmission>> {
        return flowOf(RedditResult.Loading)
    }
}
