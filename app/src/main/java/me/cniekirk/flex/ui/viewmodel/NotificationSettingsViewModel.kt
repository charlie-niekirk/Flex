package me.cniekirk.flex.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.ui.settings.state.NotificationSettingsSideEffect
import me.cniekirk.flex.ui.settings.state.NotificationSettingsState
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val flexSettings: DataStore<FlexSettings>
) : ViewModel(), ContainerHost<NotificationSettingsState, NotificationSettingsSideEffect> {

    override val container = container<NotificationSettingsState, NotificationSettingsSideEffect>(
        NotificationSettingsState()
    ) {
        init()
    }

    private fun init() = intent {
        flexSettings.data
            .catch { exception ->
                if (exception is IOException) {
                    Timber.e(exception)
                    emit(FlexSettings.getDefaultInstance())
                } else {
                    throw exception
                }
            }
            .collect { settings ->
                val profile = settings.profilesList.first { it.selected }
                reduce { state.copy(personalNotifications = profile.personalNotifications) }
            }
    }

    fun togglePersonalNotifications() = intent {
        val updated = flexSettings.updateData { settings ->
            val profile = settings.profilesList.first { it.selected }
            settings.toBuilder()
                .setProfiles(
                    settings.profilesList.indexOf(profile),
                    profile.toBuilder().setPersonalNotifications(!profile.personalNotifications)
                ).build()
        }
        val profile = updated.profilesList.first { it.selected }
        reduce { state.copy(personalNotifications = profile.personalNotifications) }
    }
}