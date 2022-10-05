package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import javax.inject.Inject

interface RemoveVoteThingUseCase {
    operator fun invoke(thingId: String): Flow<RedditResult<Boolean>>
}

class RemoveVoteThingUseCaseImpl @Inject constructor(
    private val redditDataRepository: RedditDataRepository
) : RemoveVoteThingUseCase {

    override fun invoke(thingId: String) =
        redditDataRepository.removeVoteThing(thingId)
}