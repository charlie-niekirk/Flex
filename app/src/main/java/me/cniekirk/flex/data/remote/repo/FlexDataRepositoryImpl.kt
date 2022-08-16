package me.cniekirk.flex.data.remote.repo

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.remote.FlexApi
import me.cniekirk.flex.data.remote.model.flex.Device
import me.cniekirk.flex.domain.FlexDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.settings.model.SubredditTracker
import javax.inject.Inject

class FlexDataRepositoryImpl @Inject constructor(
    private val flexApi: FlexApi
) : FlexDataRepository {

    override fun submitTracker(subredditTracker: SubredditTracker): Flow<RedditResult<Boolean>> = flow {
        val response = flexApi.submitTracker(subredditTracker)
        response.body()?.let { emit(RedditResult.Success(response.isSuccessful)) }
    }

    override fun getTrackers(device: Device): Flow<RedditResult<List<SubredditTracker>>> = flow {
        val response = flexApi.getTrackers(device)
        response.body()?.let { emit(RedditResult.Success(it)) }
    }
}