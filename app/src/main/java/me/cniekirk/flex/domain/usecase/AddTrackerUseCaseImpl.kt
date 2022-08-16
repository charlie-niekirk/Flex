package me.cniekirk.flex.domain.usecase

import me.cniekirk.flex.domain.FlexDataRepository
import me.cniekirk.flex.ui.settings.model.SubredditTracker
import javax.inject.Inject

class AddTrackerUseCaseImpl @Inject constructor(
    private val flexDataRepository: FlexDataRepository
) : AddTrackerUseCase {
    override fun invoke(subredditTracker: SubredditTracker) =
        flexDataRepository.submitTracker(subredditTracker)
}