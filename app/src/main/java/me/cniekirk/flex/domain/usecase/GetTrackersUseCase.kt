package me.cniekirk.flex.domain.usecase

import androidx.datastore.core.DataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.data.remote.model.flex.Device
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlexDataRepository
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.settings.model.SubredditTracker
import javax.inject.Inject

class GetTrackersUseCase @Inject constructor(
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher,
    private val flexDataRepository: FlexDataRepository,
    private val flexSettings: DataStore<FlexSettings>
) : FlowUseCase<SubredditTracker, List<SubredditTracker>>(coroutineDispatcher) {

    override suspend fun execute(parameters: SubredditTracker): Flow<RedditResult<List<SubredditTracker>>> {
        val device = Device(flexSettings.data.first().deviceToken)
        return flexDataRepository.getTrackers(device)
    }
}