package me.cniekirk.flex.data.remote.repo

import android.content.Context
import android.net.Uri
import androidx.annotation.IntRange
import androidx.documentfile.provider.DocumentFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.local.db.User
import me.cniekirk.flex.data.local.db.UserDao
import me.cniekirk.flex.data.local.prefs.Preferences
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.data.remote.model.MoreComments
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.data.remote.model.auth.Token
import me.cniekirk.flex.data.remote.model.base.Listing
import me.cniekirk.flex.data.remote.model.envelopes.EnvelopedCommentData
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.gallery.DownloadState
import me.cniekirk.flex.util.getHttpBasicAuthHeader
import me.cniekirk.flex.util.toAuthParams
import okio.Okio
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import android.provider.MediaStore

import android.content.ContentValues
import android.os.Build
import java.io.FileOutputStream
import java.io.InputStream


@Suppress("BlockingMethodInNonBlockingContext")
class RedditDataRepositoryImpl @Inject constructor(
    @Named("userlessApi") private val redditApi: RedditApi,
    @Named("authApi") private val authRedditApi: RedditApi,
    @Named("downloadApi") private val downloadRedditApi: RedditApi,
    @ApplicationContext private val context: Context,
    private val preferences: Preferences,
    private val userDao: UserDao
) : RedditDataRepository {

    override fun getAccessToken(code: String): Flow<RedditResult<Token>> = flow {
        val response = redditApi.getAccessToken(getHttpBasicAuthHeader(), code.toAuthParams())
        userDao.insert(User(response.accessToken, response.refreshToken!!))
        emit(RedditResult.Success(response))
    }

    override fun upvoteThing(thingId: String) = vote(thingId, 1)

    override fun removeVoteThing(thingId: String) = vote(thingId, 0)

    override fun downvoteThing(thingId: String) = vote(thingId, -1)

    override fun downloadMedia(url: String): Flow<RedditResult<DownloadState>> = flow {
        preferences.downloadDirFlow.collect {
            if (it.isEmpty()) {
                emit(RedditResult.Success(DownloadState.NoDefinedLocation))
            } else {
                // Actually download
                val response = downloadRedditApi.downloadMedia(url)
                if (response.isSuccessful) {
                    val treeUri = context.contentResolver.persistedUriPermissions.firstOrNull()?.uri
                    treeUri?.let { uri ->
                        // Get selected directory and create the file
                        val directory = DocumentFile.fromTreeUri(context, uri)
                        val file = directory?.createFile(response.headers()["content-type"] ?: "image/jpeg",
                            UUID.randomUUID().toString().replace("-", "") + url.substring(url.lastIndexOf(".")))

                        file?.let {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                response.body()?.byteStream()?.use { input ->
                                    FileOutputStream(file.uri.toString()).use { output ->
                                        input.copyTo(output)
                                        emit(RedditResult.Success(DownloadState.Success))
                                    }
                                }
                            } else {
                                response.body()?.byteStream()?.use { input ->
                                    context.contentResolver.openOutputStream(file.uri)?.use { output ->
                                        input.copyTo(output)
                                    }
                                }
                            }
                            emit(RedditResult.Success(DownloadState.Success))
                        } ?: run {
                            emit(RedditResult.Error(Exception("Unknown!")))
                        }

                    } ?: run {
                        emit(RedditResult.Success(DownloadState.NoDefinedLocation))
                    }
                } else {
                    emit(RedditResult.Error(IOException(response.message())))
                }
            }
        }
    }

    private fun vote(thingId: String, @IntRange(from = -1, to = 1) direction: Int): Flow<RedditResult<Boolean>> = flow {
        userDao.getAll().firstOrNull()?.let {
            authRedditApi.vote("Bearer ${it.accessToken}", thingId, direction)
            emit(RedditResult.Success(true))
        } ?: run {
            emit(RedditResult.UnAuthenticated)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getComments(submissionId: String, sortType: String): Flow<RedditResult<List<Comment>>> = flow {
        val response = if (userDao.getAll().isNullOrEmpty()) {
            redditApi.getCommentsForListing(submissionId, sortType)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            authRedditApi.getCommentsForListing(submissionId, sortType, accessToken)
        }
        val commentTree = response.lastOrNull()?.data as Listing<EnvelopedCommentData>
        val comments = mutableListOf<Comment>()
        buildTree(commentTree, comments)
        emit(RedditResult.Success(comments))
    }

    private fun buildTree(data: Listing<EnvelopedCommentData>, output: MutableList<Comment>) {
        data.children.forEach {
            val topLevel = it.data
            if (topLevel !is MoreComments) {
                output.add(topLevel as Comment)
                if (!topLevel.replies.isNullOrEmpty()) {
                    buildTree(topLevel.repliesRaw!!.data, output)
                }
            }
        }
    }
}