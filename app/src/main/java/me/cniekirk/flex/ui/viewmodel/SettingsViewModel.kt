package me.cniekirk.flex.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.FlexSettings.Profile
import me.cniekirk.flex.ui.settings.state.SettingsSideEffect
import me.cniekirk.flex.ui.settings.state.SettingsState
import me.cniekirk.flex.ui.settings.state.toUiSettings
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val flexSettings: DataStore<FlexSettings>
) : ViewModel(), ContainerHost<SettingsState, SettingsSideEffect> {

    override val container = container<SettingsState, SettingsSideEffect>(
        SettingsState()
    ) {
        init()
    }

    private fun init() = intent {
        flexSettings.data
            .catch { exception ->
                if (exception is IOException) {
                    Timber.e(exception)
                    val profile = Profile.newBuilder()
                        .setBlurNsfw(true)
                        .setSelected(true)
                        .setName("Default")
                        .setShowPreviews(true)
                        .setPersonalNotifications(false)
                        .build()
                    val settings = FlexSettings.newBuilder().setProfiles(0, profile).build()
                    emit(settings)
                } else {
                    throw exception
                }
            }
            .collect { flexSettings ->
                reduce { state.copy(settings = flexSettings.toUiSettings()) }
            }
    }

    fun setBlurNsfw() = intent {
        flexSettings.updateData { settings ->
            val profile = settings.profilesList.first { it.selected }
            settings.toBuilder().setProfiles(
                settings.profilesList.indexOf(profile),
                profile.toBuilder()
                    .setBlurNsfw(profile.blurNsfw.not())
                    .build())
                .build()
        }
    }

    fun notificationsClicked() = intent {
        postSideEffect(SettingsSideEffect.NotificationsClicked)
    }
}