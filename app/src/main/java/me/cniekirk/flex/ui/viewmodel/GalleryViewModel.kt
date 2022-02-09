package me.cniekirk.flex.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.imgur.ImageData
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import me.cniekirk.flex.data.remote.model.reddit.rules.Rules
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.DownloadMediaUseCase
import me.cniekirk.flex.domain.usecase.GetImgurAlbumImagesUseCase
import me.cniekirk.flex.ui.gallery.DownloadState
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val downloadMediaUseCase: DownloadMediaUseCase,
    private val getImgurAlbumImagesUseCase: GetImgurAlbumImagesUseCase
) : ViewModel() {

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    private val _imgurGallery = MutableSharedFlow<RedditResult<ImgurResponse<List<ImageData>>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val imgurGallery: Flow<RedditResult<ImgurResponse<List<ImageData>>>> = _imgurGallery.distinctUntilChanged()

    fun getImgurGallery(albumHash: String) {
        viewModelScope.launch {
            getImgurAlbumImagesUseCase(albumHash).collect {
                _imgurGallery.emit(it)
            }
        }
    }

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
            //preferences.setDownloadDirectory(pathString!!)
        }
    }

    fun resetState() {
        _downloadState.value = DownloadState.Idle
    }
}