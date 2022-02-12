package me.cniekirk.flex.data.local.repo

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.domain.LocalDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.gallery.DownloadState
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject

class LocalDataRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : LocalDataRepository {

    override fun saveImage(subredditName: String, image: Bitmap): Flow<RedditResult<DownloadState>> = flow {
        val treeUri = context.contentResolver.persistedUriPermissions.firstOrNull()?.uri
        treeUri?.let {
            // Get selected directory and create the file
            val directory = DocumentFile.fromTreeUri(context, it)
            val subredditDirectory = directory?.createDirectory(subredditName)
            val postsDirectory = subredditDirectory?.createDirectory("posts")
            val file = postsDirectory?.createFile("image/jpeg",
                UUID.randomUUID().toString().replace("-", ""))

            file?.let {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    FileOutputStream(file.uri.toString()).use { output ->
                        image.compress(Bitmap.CompressFormat.PNG, 0, output)
                    }
                } else {
                    context.contentResolver.openOutputStream(file.uri)?.use { output ->
                        image.compress(Bitmap.CompressFormat.PNG, 0, output)
                    }
                }
                emit(RedditResult.Success(DownloadState.Success))
            } ?: run {
                emit(RedditResult.Error(Exception("Unknown!")))
            }
        } ?: run { emit(RedditResult.Success(DownloadState.NoDefinedLocation)) }
    }
}