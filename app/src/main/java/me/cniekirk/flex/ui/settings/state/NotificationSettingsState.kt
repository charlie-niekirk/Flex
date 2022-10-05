package me.cniekirk.flex.ui.settings.state

data class NotificationSettingsState(
    val subredditTrackers: List<String> = emptyList(),
    val personalNotifications: Boolean = false
)

sealed class NotificationSettingsSideEffect {
    object NavigateToCreateSubredditTracker : NotificationSettingsSideEffect()
}