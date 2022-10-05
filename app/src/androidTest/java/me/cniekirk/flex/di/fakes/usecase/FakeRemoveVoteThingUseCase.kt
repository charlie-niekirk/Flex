package me.cniekirk.flex.di.fakes.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.RemoveVoteThingUseCase
import javax.inject.Inject

class FakeRemoveVoteThingUseCase @Inject constructor() : RemoveVoteThingUseCase {

    override fun invoke(thingId: String): Flow<RedditResult<Boolean>> {
        return flowOf(RedditResult.Success(true))
    }
}
