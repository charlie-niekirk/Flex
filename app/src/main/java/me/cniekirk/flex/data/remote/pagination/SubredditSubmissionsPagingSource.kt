package me.cniekirk.flex.data.remote.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import coil.ImageLoader
import coil.request.ImageRequest
import me.cniekirk.flex.data.local.db.UserDao
import me.cniekirk.flex.data.remote.GfycatApi
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.StreamableApi
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.util.Link
import me.cniekirk.flex.util.processLinkInternal
import timber.log.Timber

class SubredditSubmissionsPagingSource(
    private val redditApi: RedditApi,
    private val authRedditApi: RedditApi,
    private val streamableApi: StreamableApi,
    private val gfycatApi: GfycatApi,
    private val subreddit: String,
    private val sortType: String,
    private val userDao: UserDao,
    private val imageRequest: ImageRequest.Builder,
    private val imageLoader: ImageLoader
) : PagingSource<String, AuthedSubmission>() {

    private var before: String? = null
    private var after: String? = null
    private var itemCount: Int = 0

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AuthedSubmission> {
        return try {

            val response = if (userDao.getAll().isNullOrEmpty()) {
                if (params.key.equals(before, true)) {
                    if (itemCount < 10) {
                        // First page
                        redditApi.getPosts(subreddit, sortType, count = itemCount, limit = 10)
                    } else {
                        redditApi.getPosts(subreddit, sortType, before = params.key, count = itemCount, limit = 10)
                    }
                } else {
                    redditApi.getPosts(subreddit, sortType, after = params.key, count = itemCount, limit = 10)
                }
            } else {
                val accessToken = "Bearer ${userDao.getAll().first().accessToken}"
                if (params.key.equals(before, true)) {
                    if (itemCount < 10) {
                        // First page
                        authRedditApi.getPosts(subreddit, sortType, count = itemCount, limit = 10, authorization = accessToken)
                    } else {
                        authRedditApi.getPosts(subreddit, sortType, before = params.key, count = itemCount, limit = 10, authorization = accessToken)
                    }
                } else {
                    authRedditApi.getPosts(subreddit, sortType, after = params.key, count = itemCount, limit = 10, authorization = accessToken)
                }
            }

            response.data.children.map {
                it.data.url.processLinkInternal { link ->
                    when (link) {
                        Link.StreamableLink -> {
                            val shortcode = it.data.url.substring(it.data.url.lastIndexOf("/") + 1)
                            val streamableUrl = streamableApi.getStreamableDetails(shortcode)
                            if (streamableUrl.isSuccessful) {
                                if (streamableUrl.body()?.files?.containsKey("mp4")!!) {
                                    it.data = it.data.copy(url = streamableUrl.body()?.files?.get("mp4")?.url ?: "")
                                }
                            }
                        }
                        Link.GfycatLink -> {
                            val gfyid = it.data.url.substring(it.data.url.lastIndexOf("/") + 1)
                            val gfycatLinks = gfycatApi.getGfycatLinks(gfyid)
                            if (gfycatLinks.isSuccessful) {
                                if (gfycatLinks.body()?.gfyItem?.contentUrls?.containsKey("mobile")!!) {
                                    Timber.d("URL: ${gfycatLinks.body()?.gfyItem?.contentUrls?.get("mobile")?.url}")
                                    it.data = it.data.copy(url = gfycatLinks.body()?.gfyItem?.contentUrls?.get("mobile")?.url ?: "")
                                }
                            }
                        }
                        is Link.ImageLink -> {
                            if (!params.key.equals(before, true)) {
                                imageLoader.prefetch(link.url)
                            }
                        }
                        else -> {}
                    }
                }

            }

//            if (!params.key.equals(before, true)) {
//                // Pre-load any images
//                when (response) {
//                    is RedditResult.Success<*> -> {
//                        val posts = response.data.children
//                        posts.map {
//                            it.data.url.processLink { linkType ->
//                                when (linkType) {
//                                    is Link.ImageLink -> imageLoader.prefetch(linkType.url)
//                                    else -> {}
//                                }
//                            }
//                        }
//                    }
//                }
//            }

            if (itemCount > 0 && params.key.equals(before, true)) {
                itemCount -= response.data.dist
            } else {
                itemCount += response.data.dist
            }

            after = response.data.after
            before = response.data.before

            LoadResult.Page(
                response.data.children.map { it.data },
                before,
                after)
        } catch (exception: Exception) {
            Timber.e(exception)
            LoadResult.Error(exception)
        }
    }

    private fun ImageLoader.prefetch(url: String) {
        val request = imageRequest
            .data(url)
            .build()
        enqueue(request)
    }

    override fun getRefreshKey(state: PagingState<String, AuthedSubmission>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id.toString()
        }
    }
}