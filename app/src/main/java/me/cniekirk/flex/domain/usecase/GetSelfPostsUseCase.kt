package me.cniekirk.flex.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission

interface GetSelfPostsUseCase {
    operator fun invoke(username: String): Flow<PagingData<AuthedSubmission>>
}