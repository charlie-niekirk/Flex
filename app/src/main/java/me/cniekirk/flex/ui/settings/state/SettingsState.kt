package me.cniekirk.flex.ui.settings.state

import me.cniekirk.flex.FlexSettings

data class SettingsState(
    val settings: FlexSettings = FlexSettings.getDefaultInstance()
)

sealed class SettingsSideEffect {
    object NotificationsClicked : SettingsSideEffect()
}