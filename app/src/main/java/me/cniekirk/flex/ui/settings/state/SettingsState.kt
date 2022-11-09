package me.cniekirk.flex.ui.settings.state

import me.cniekirk.flex.FlexSettings

data class SettingsState(
    val settings: UiSettings = UiSettings(listOf(Settings(true, false, "")))
)

data class UiSettings(
    val profiles: List<Settings>
)

data class Settings(
    val selected: Boolean,
    val blurNsfw: Boolean,
    val name: String
)

fun FlexSettings.toUiSettings(): UiSettings {
    val profiles = this.profilesList?.map { profile ->
        Settings(
            selected = profile.selected,
            blurNsfw = profile.blurNsfw,
            name = profile.name
        )
    } ?: listOf()
    return UiSettings(profiles)
}

sealed class SettingsSideEffect {
    object NotificationsClicked : SettingsSideEffect()
}