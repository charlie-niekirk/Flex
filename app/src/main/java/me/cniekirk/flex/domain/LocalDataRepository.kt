package me.cniekirk.flex.domain

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import me.cniekirk.flex.ui.gallery.DownloadState

interface LocalDataRepository {

    fun saveImage(subredditName: String, image: Bitmap): Flow<RedditResult<DownloadState>>
}