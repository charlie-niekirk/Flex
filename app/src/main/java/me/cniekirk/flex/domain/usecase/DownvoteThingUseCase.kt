package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import javax.inject.Inject

interface DownvoteThingUseCase {
    operator fun invoke(thingId: String): Flow<RedditResult<Boolean>>
}

class DownvoteThingUseCaseImpl @Inject constructor(
    private val redditDataRepository: RedditDataRepository
) : DownvoteThingUseCase {

    override fun invoke(thingId: String) =
        redditDataRepository.downvoteThing(thingId)

}