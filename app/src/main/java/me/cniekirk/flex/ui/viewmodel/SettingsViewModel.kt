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
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val flexSettings: DataStore<FlexSettings>
) : ViewModel() {

    val settings: Flow<FlexSettings> = flexSettings.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception)
                emit(FlexSettings.getDefaultInstance())
            } else {
                throw exception
            }
        }

    fun setBlurNsfw() {
        viewModelScope.launch {
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
    }
}