package me.cniekirk.flex.di.fakes.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.domain.usecase.GetSelfPostsUseCase
import javax.inject.Inject

class FakeGetSelfPostsUseCase @Inject constructor() : GetSelfPostsUseCase {

    override fun invoke(username: String): Flow<PagingData<AuthedSubmission>> {
        return flowOf(PagingData.empty())
    }
}
