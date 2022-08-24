package me.cniekirk.flex.di.fakes.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.UpvoteThingUseCase
import javax.inject.Inject

class FakeUpvoteThingUseCase @Inject constructor() : UpvoteThingUseCase {

    override fun invoke(thingId: String): Flow<RedditResult<Boolean>> {
        return flowOf(RedditResult.Success(true))
    }
}
