package me.cniekirk.flex.domain

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.data.remote.model.flex.Device
import me.cniekirk.flex.ui.settings.model.SubredditTracker

interface FlexDataRepository {

    fun submitTracker(subredditTracker: SubredditTracker): Flow<RedditResult<Boolean>>

    fun getTrackers(device: Device): Flow<RedditResult<List<SubredditTracker>>>
}