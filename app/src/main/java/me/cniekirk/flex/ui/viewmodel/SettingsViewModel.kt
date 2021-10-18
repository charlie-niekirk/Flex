package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.local.prefs.Preferences
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: Preferences
) : ViewModel() {

    val shouldBlurNsfw: StateFlow<Boolean?> = preferences.blurNsfwFlow.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        true)

    fun setBlurNsfw() {
        viewModelScope.launch {
            preferences.setShouldBlurNsfw(shouldBlurNsfw.value?.not() ?: true)
        }
    }

}