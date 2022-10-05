package me.cniekirk.flex.ui.settings.state

import me.cniekirk.flex.ui.settings.model.SubredditTracker

data class CreateSubredditTrackerState(
    val tracker: SubredditTracker? = null
)

sealed class CreateSubredditTrackerEffect {
    data class AddTrackerError(val error: String) : CreateSubredditTrackerEffect()
    object TrackerAddedOrUpdated : CreateSubredditTrackerEffect()
}