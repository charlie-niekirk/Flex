package me.cniekirk.flex.di.fakes.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.MoreCommentsRequest
import me.cniekirk.flex.domain.usecase.GetMoreCommentsUseCase
import javax.inject.Inject

class FakeGetMoreCommentsUseCase @Inject constructor() : GetMoreCommentsUseCase {

    override fun invoke(moreCommentsRequest: MoreCommentsRequest): Flow<RedditResult<List<CommentData>>> {
        return flowOf(RedditResult.Success(listOf()))
    }
}
