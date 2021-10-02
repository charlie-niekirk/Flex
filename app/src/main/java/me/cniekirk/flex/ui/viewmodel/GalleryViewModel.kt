package me.cniekirk.flex.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.local.prefs.Preferences
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.DownloadMediaUseCase
import me.cniekirk.flex.ui.gallery.DownloadState
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val downloadMediaUseCase: DownloadMediaUseCase,
    private val preferences: Preferences
) : ViewModel() {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    fun download(url: String) {
        viewModelScope.launch {
            downloadMediaUseCase(url).collect {
                when (it) {
                    is RedditResult.Success -> {
                        _downloadState.value = it.data
                    }
                    else -> { }
                }
            }
        }
    }

    fun registerDownloadLocation(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            val pathString = uri.encodedPath
            preferences.setDownloadDirectory(pathString!!)
        }
    }

    fun resetState() {
        _downloadState.value = DownloadState.Idle
    }

}