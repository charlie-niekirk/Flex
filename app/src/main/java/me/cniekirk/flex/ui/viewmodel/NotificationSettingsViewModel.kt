package me.cniekirk.flex.ui.viewmodel

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.cniekirk.flex.FlexSettings
import org.orbitmvi.orbit.ContainerHost
import javax.inject.Inject

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val flexSettings: DataStore<FlexSettings>
) : ViewModel(), ContainerHost<>() {
}