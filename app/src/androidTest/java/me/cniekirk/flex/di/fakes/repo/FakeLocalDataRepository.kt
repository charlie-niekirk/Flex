package me.cniekirk.flex.di.fakes.repo

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.domain.LocalDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.gallery.DownloadState
import javax.inject.Inject

class FakeLocalDataRepository @Inject constructor() : LocalDataRepository {

    override fun saveImage(
        subredditName: String,
        image: Bitmap
    ): Flow<RedditResult<DownloadState>> {
        return flowOf(RedditResult.Success(DownloadState.Success))
    }
}
