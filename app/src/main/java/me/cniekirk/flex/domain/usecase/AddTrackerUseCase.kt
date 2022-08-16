package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.settings.model.SubredditTracker

interface AddTrackerUseCase {
    operator fun invoke(subredditTracker: SubredditTracker): Flow<RedditResult<Boolean>>
}