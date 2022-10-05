package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import javax.inject.Inject

interface UpvoteThingUseCase {
    operator fun invoke(thingId: String): Flow<RedditResult<Boolean>>
}

class UpvoteThingUseCaseImpl @Inject constructor(
    private val redditDataRepository: RedditDataRepository
) : UpvoteThingUseCase {

    override fun invoke(thingId: String) =
        redditDataRepository.upvoteThing(thingId)
}