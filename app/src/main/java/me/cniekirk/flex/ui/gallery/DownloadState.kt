package me.cniekirk.flex.ui.gallery

sealed class DownloadState {
    object Idle : DownloadState()
    object NoDefinedLocation : DownloadState()
    object Success : DownloadState()
}
