package me.cniekirk.flex.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.cniekirk.flex.domain.LocalDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.gallery.DownloadState
import javax.inject.Inject

@HiltViewModel
class ShareAsImageViewModel @Inject constructor(
    private val localDataRepository: LocalDataRepository
): ViewModel() {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    fun saveImage(subredditName: String, image: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            localDataRepository.saveImage(subredditName, image)
                .collect {
                    when (it) {
                        is RedditResult.Success -> {
                            _downloadState.value = it.data
                        }
                        is RedditResult.Error -> {

                        }
                    }
                }
        }
    }

}