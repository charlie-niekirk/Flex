package me.cniekirk.flex.domain.usecase

import me.cniekirk.flex.domain.RedditDataRepository
import javax.inject.Inject

class GetThingInfoUseCaseImpl @Inject constructor(
    private val redditDataRepository: RedditDataRepository
) : GetThingInfoUseCase {

    override fun invoke(postId: String) =
        redditDataRepository.getPostInfo(postId)
}