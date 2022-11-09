package me.cniekirk.flex.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.cniekirk.flex.FlexSettings.Profile
import me.cniekirk.flex.R
import me.cniekirk.flex.ui.settings.state.Settings
import me.cniekirk.flex.ui.settings.state.SettingsSideEffect
import me.cniekirk.flex.ui.settings.state.SettingsState
import me.cniekirk.flex.ui.viewmodel.SettingsViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber

@Composable
fun SettingsPage(viewModel: SettingsViewModel = viewModel()) {
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            SettingsSideEffect.NotificationsClicked -> {
                // ??
            }
        }
    }

    SettingsPageContent(state, viewModel::setBlurNsfw)
}

@Composable
fun SettingsPageContent(
    state: State<SettingsState>,
    onBlurNsfw: () -> Unit
) {
    val profiles = state.value.settings.profiles

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.title_settings),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.padding(top = 16.dp, start = 16.dp),
            text = stringResource(id = R.string.settings_profiles_title),
            style = MaterialTheme.typography.bodySmall
        )
        Divider(modifier = Modifier.padding(top = 8.dp))
        profiles.forEach { profile ->
            SettingsProfile(settingsProfile = profile)
        }

        Divider(modifier = Modifier.padding(top = 8.dp))
        SettingsItem(
            settingName = "Blur NSFW",
            selected = state.value.settings.profiles.firstOrNull { it.selected }?.blurNsfw ?: false
        ) {
            onBlurNsfw()
        }
    }
}

@Composable
fun SettingsItem(
    settingName: String,
    selected: Boolean,
    onSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = settingName,
            style = MaterialTheme.typography.bodySmall
        )
        Switch(
            modifier = Modifier.padding(top = 8.dp),
            checked = selected,
            onCheckedChange = { onSelected() }
        )
    }
}

@Composable
fun SettingsProfile(settingsProfile: Settings) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = settingsProfile.name,
            )
            Crossfade(targetState = settingsProfile.selected) {
                if (it) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(id = R.string.tick)
                    )
                }
            }
        }
        Divider()
    }
}