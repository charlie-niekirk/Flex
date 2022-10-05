package me.cniekirk.flex.di.fakes.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import me.cniekirk.flex.domain.usecase.GetCommentsUseCase
import javax.inject.Inject

class FakeGetCommentsUseCase @Inject constructor() : GetCommentsUseCase {

    override fun invoke(commentRequest: CommentRequest): Flow<RedditResult<List<CommentData>>> {
        return flowOf(RedditResult.Success(
            listOf(
                Comment(
                    null,
                    null,
                    emptyList(),
                    "chertycherty",
                    "This is a test post",
                    null,
                    null,
                    1661306240,
                    1661306240,
                    null,
                    0,
                    null,
                    isArchived = false,
                    isLocked = false,
                    isSaved = false,
                    isScoreHidden = false,
                    isStickied = false,
                    isSubmitter = false,
                    likes = false,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    score = 10,
                    subreddit = "flexforreddit",
                    subredditId = null,
                    subredditNamePrefixed = "r/flexforreddit",
                    isCollapsed = false
                )
            )
        ))
    }
}
