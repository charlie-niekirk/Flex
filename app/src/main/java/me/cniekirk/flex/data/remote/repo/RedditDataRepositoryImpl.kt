package me.cniekirk.flex.data.remote.repo

import android.content.Context
import android.os.Build
import androidx.annotation.IntRange
import androidx.datastore.core.DataStore
import androidx.documentfile.provider.DocumentFile
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.R
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.dao.UserDao
import me.cniekirk.flex.data.local.db.entity.User
import me.cniekirk.flex.data.remote.*
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.data.remote.model.reddit.auth.RedditUser
import me.cniekirk.flex.data.remote.model.reddit.auth.Token
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentData
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedContributionListing
import me.cniekirk.flex.data.remote.model.reddit.flair.UserFlairItem
import me.cniekirk.flex.data.remote.model.reddit.rules.Rules
import me.cniekirk.flex.data.remote.model.reddit.subreddit.ModUser
import me.cniekirk.flex.data.remote.model.reddit.subreddit.Subreddit
import me.cniekirk.flex.data.remote.model.wikipedia.WikiSummary
import me.cniekirk.flex.data.remote.pagination.UserSubmissionsPagingSource
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.gallery.DownloadState
import me.cniekirk.flex.util.getHttpBasicAuthHeader
import me.cniekirk.flex.util.toAuthParams
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Named


@Suppress("BlockingMethodInNonBlockingContext")
class RedditDataRepositoryImpl @Inject constructor(
    @Named("preAuthApi") private val redditApi: RedditApi,
    @Named("preLoginApi") private val preLoginRedditApi: RedditApi,
    @Named("loginApi") private val authRedditApi: RedditApi,
    @Named("downloadApi") private val downloadRedditApi: RedditApi,
    private val wikipediaApi: WikipediaApi,
    private val streamableApi: StreamableApi,
    private val imgurApi: ImgurApi,
    private val gfycatApi: GfycatApi,
    private val redGifsApi: RedGifsApi,
    private val twitterApi: TwitterApi,
    @ApplicationContext private val context: Context,
    private val preLoginUserDao: PreLoginUserDao,
    private val userDao: UserDao,
    private val flexSettings: DataStore<FlexSettings>
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
        val treeUri = context.contentResolver.persistedUriPermissions.firstOrNull()?.uri
        treeUri?.let {
            // Actually download
            val response = downloadRedditApi.downloadMedia(url)
            if (response.isSuccessful) {
                // Get selected directory and create the file
                val directory = DocumentFile.fromTreeUri(context, it)
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
            } else {
                emit(RedditResult.Error(IOException(response.message())))
            }
        } ?: run { emit(RedditResult.Success(DownloadState.NoDefinedLocation)) }
    }

    override fun getMe(): Flow<RedditResult<RedditUser>> = flow {
        userDao.getAll().firstOrNull()?.let {
            val response = authRedditApi.getMe("Bearer ${it.accessToken}")
            emit(RedditResult.Success(response))
        } ?: run {
            emit(RedditResult.UnAuthenticated)
        }
    }

    override fun getSelfPosts(username: String): Flow<PagingData<AuthedSubmission>> {
        userDao.getAll().firstOrNull()?.let {
            val pager = Pager(
                config = PagingConfig(pageSize = 15, prefetchDistance = 5),
                pagingSourceFactory = {
                    UserSubmissionsPagingSource(
                        authRedditApi,
                        streamableApi,
                        imgurApi,
                        gfycatApi,
                        redGifsApi,
                        twitterApi,
                        username,
                        userDao
                    )
                }
            )
            return pager.flow
        } ?: run {
            return flowOf()
        }
    }

    override suspend fun getWikipediaSummary(article: String): RedditResult<WikiSummary> {
        val response = wikipediaApi.getWikiArticleDetails(article)
        return RedditResult.Success(response)
    }

    private fun vote(thingId: String, @IntRange(from = -1, to = 1) direction: Int): Flow<RedditResult<Boolean>> = flow {
        userDao.getAll().firstOrNull()?.let {
            authRedditApi.vote("Bearer ${it.accessToken}", thingId, direction)
            emit(RedditResult.Success(true))
        } ?: run {
            emit(RedditResult.UnAuthenticated)
        }
    }

    override suspend fun getComments(submissionId: String, sortType: String): List<EnvelopedContributionListing> {
        val response = if (userDao.getAll().isNullOrEmpty()) {
            val accessToken = "Bearer ${preLoginUserDao.getAll().firstOrNull()?.accessToken}"
            preLoginRedditApi.getCommentsForListing(submissionId, sortType, authorization = accessToken)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            authRedditApi.getCommentsForListing(submissionId, sortType, accessToken)
        }
        return response
    }

    override fun getMoreComments(moreComments: MoreComments, parentId: String): Flow<RedditResult<List<CommentData>>> = flow {
        val children = moreComments.children.joinToString(",")
        val response = if (userDao.getAll().isNullOrEmpty()) {
            val accessToken = "Bearer ${preLoginUserDao.getAll().firstOrNull()?.accessToken}"
            preLoginRedditApi.getMoreComments(linkId = parentId, children = children, authorization = accessToken)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            authRedditApi.getMoreComments(linkId = parentId, children = children, authorization = accessToken)
        }
        if (response.json.errors.isNullOrEmpty()) {
            val commentTree = response.json.data.things as List<EnvelopedCommentData>
            emit(RedditResult.Success(commentTree.map { it.data }))
        } else {
            emit(RedditResult.Error(RuntimeException("Unknown error!")))
        }
    }

    override fun searchSubreddits(query: String, sortType: String): Flow<RedditResult<List<Subreddit>>> = flow {
        val response = if (userDao.getAll().isNullOrEmpty()) {
            val accessToken = "Bearer ${preLoginUserDao.getAll().firstOrNull()?.accessToken}"
            preLoginRedditApi.searchSubreddits(query = query, sort = sortType, nsfw = false, authorization = accessToken)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            authRedditApi.searchSubreddits(query = query, sort = sortType, nsfw = false, authorization = accessToken)
        }

        emit(RedditResult.Success(response.data.children.map { it.data }.filter { it.subscribers != null }))
    }

    override fun getSubredditRules(subreddit: String): Flow<RedditResult<Rules>> = flow {
        val response = if (userDao.getAll().isNullOrEmpty()) {
            val accessToken = "Bearer ${preLoginUserDao.getAll().firstOrNull()?.accessToken}"
            preLoginRedditApi.getSubredditRules(accessToken, subreddit)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            authRedditApi.getSubredditRules(accessToken, subreddit)
        }
        emit(RedditResult.Success(response))
    }

    override fun getSubredditInfo(subreddit: String): Flow<RedditResult<Subreddit>> = flow {
        val response = if (userDao.getAll().isNullOrEmpty()) {
            val accessToken = "Bearer ${preLoginUserDao.getAll().firstOrNull()?.accessToken}"
            preLoginRedditApi.getSubredditInfo(accessToken, subreddit)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            authRedditApi.getSubredditInfo(accessToken, subreddit)
        }
        emit(RedditResult.Success(response.data))
    }

    override fun getSubredditModerators(subreddit: String): Flow<RedditResult<List<ModUser>>> = flow {
        if (userDao.getAll().isNullOrEmpty()) {
            emit(RedditResult.UnAuthenticated)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            val response = authRedditApi.getSubredditModerators(accessToken, subreddit)
            emit(RedditResult.Success(response.data.children))
        }
    }

    override fun subscribeSubreddit(subredditId: String): Flow<RedditResult<Int>> = flow {
        if (userDao.getAll().isNullOrEmpty()) {
            emit(RedditResult.UnAuthenticated)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            val response = authRedditApi.subscribeAction(accessToken, "sub", subredditId)
            if (response.isSuccessful) {
                emit(RedditResult.Success(R.string.subreddit_subscribe_success))
            } else {
                emit(RedditResult.Error(RuntimeException("Unknown!")))
            }
        }
    }

    override fun unsubscribeSubreddit(subredditId: String): Flow<RedditResult<Int>> = flow {
        if (userDao.getAll().isNullOrEmpty()) {
            emit(RedditResult.UnAuthenticated)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            val response = authRedditApi.subscribeAction(accessToken, "unsub", subredditId)
            if (response.isSuccessful) {
                emit(RedditResult.Success(R.string.subreddit_unsubscribe_success))
            } else {
                emit(RedditResult.Error(RuntimeException("Unknown!")))
            }
        }
    }

    override fun favoriteSubreddit(subreddit: String): Flow<RedditResult<Int>> = flow {
        if (userDao.getAll().isNullOrEmpty()) {
            emit(RedditResult.UnAuthenticated)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            val response = authRedditApi.favoriteAction(accessToken, true, subreddit)
            if (response.isSuccessful) {
                emit(RedditResult.Success(R.string.subreddit_favorite_success))
            } else {
                emit(RedditResult.Error(RuntimeException("Unknown!")))
            }
        }
    }

    override fun unfavoriteSubreddit(subreddit: String): Flow<RedditResult<Int>> = flow {
        if (userDao.getAll().isNullOrEmpty()) {
            emit(RedditResult.UnAuthenticated)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            val response = authRedditApi.favoriteAction(accessToken, false, subreddit)
            if (response.isSuccessful) {
                emit(RedditResult.Success(R.string.subreddit_unfavorite_success))
            } else {
                emit(RedditResult.Error(RuntimeException("Unknown!")))
            }
        }
    }

    override fun getAvailableUserFlairs(subreddit: String): Flow<RedditResult<List<UserFlairItem>>> = flow {
        if (userDao.getAll().isNullOrEmpty()) {
            emit(RedditResult.UnAuthenticated)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            val response = authRedditApi.getAvailableUserFlairs(accessToken, subreddit)
            emit(RedditResult.Success(response))
        }
    }

    override fun submitComment(markdown: String, parentThing: String): Flow<RedditResult<CommentData>> = flow {
        if (userDao.getAll().isNullOrEmpty()) {
            emit(RedditResult.UnAuthenticated)
        } else {
            val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
            val response = authRedditApi.submitComment(accessToken, markdown, parentThing)
            emit(RedditResult.Success(response))
        }
    }
}