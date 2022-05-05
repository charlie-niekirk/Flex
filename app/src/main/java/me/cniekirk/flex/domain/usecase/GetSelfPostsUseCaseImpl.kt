package me.cniekirk.flex.domain.usecase

import me.cniekirk.flex.domain.RedditDataRepository
import javax.inject.Inject

class GetSelfPostsUseCaseImpl @Inject constructor(
    private val redditDataRepository: RedditDataRepository
): GetSelfPostsUseCase {
    override fun invoke(username: String) = redditDataRepository.getSelfPosts(username)
}