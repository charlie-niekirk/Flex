package me.cniekirk.flex.data.remote.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.cniekirk.flex.data.local.db.dao.PreLoginUserDao
import me.cniekirk.flex.data.local.db.dao.UserDao
import me.cniekirk.flex.data.remote.*
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.domain.ImgurDataRepository
import me.cniekirk.flex.util.Link
import me.cniekirk.flex.util.processLinkInternal
import timber.log.Timber

class SubredditSubmissionsPagingSource(
    private val redditApi: RedditApi,
    private val authRedditApi: RedditApi,
    private val streamableApi: StreamableApi,
    private val imgurApi: ImgurApi,
    private val gfycatApi: GfycatApi,
    private val redGifsApi: RedGifsApi,
    private val twitterApi: TwitterApi,
    private val subreddit: String,
    private val sortType: String,
    private val preLoginUserDao: PreLoginUserDao,
    private val userDao: UserDao
) : PagingSource<String, AuthedSubmission>() {

    private var before: String? = null
    private var after: String? = null
    private var itemCount: Int = 0

    override suspend fun load(params: LoadParams<String>): LoadResult<String, AuthedSubmission> {
        return try {

            val response = if (userDao.getAll().isNullOrEmpty()) {
                val accessToken = "Bearer ${preLoginUserDao.getAll().firstOrNull()?.accessToken}"
                if (params.key.equals(before, true)) {
                    if (itemCount < 10) {
                        // First page
                        redditApi.getPosts(subreddit, sortType, count = itemCount, limit = 10, authorization = accessToken)
                    } else {
                        redditApi.getPosts(subreddit, sortType, before = params.key, count = itemCount, limit = 10, authorization = accessToken)
                    }
                } else {
                    redditApi.getPosts(subreddit, sortType, after = params.key, count = itemCount, limit = 10, authorization = accessToken)
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
                                    it.data = it.data.copy(url = gfycatLinks.body()?.gfyItem?.contentUrls?.get("mobile")?.url ?: "")
                                }
                            }
                        }
                        Link.RedGifLink -> {
                            val gfyid = it.data.url.substring(it.data.url.lastIndexOf("/") + 1)
                            val gfycatLinks = redGifsApi.getDirectLinks(gfyid)
                            if (gfycatLinks.isSuccessful) {
                                if (gfycatLinks.body()?.gfyItem?.contentUrls?.containsKey("mobile")!!) {
                                    it.data = it.data.copy(
                                        url = gfycatLinks.body()?.gfyItem?.contentUrls?.get("mobile")?.url ?: "")
                                }
                            }
                        }
                        is Link.ImgurGalleryLink -> {
                            val imgurResponse = imgurApi.getGalleryImages(link.albumId)
                            it.data = it.data.copy(imgurGalleryLinks = imgurResponse.data?.map { image -> image.link })
                        }
                        is Link.TwitterLink -> {
                            val tweet = twitterApi.getTweet(link.url)
                            it.data = it.data.copy(tweetDetails = tweet)
                        }
                        else -> { /* No-op */ }
                    }
                }
            }

            if (itemCount > 0 && params.key.equals(before, true)) {
                itemCount -= response.data.dist!!
            } else {
                itemCount += response.data.dist!!
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

    override fun getRefreshKey(state: PagingState<String, AuthedSubmission>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            Timber.d("Refresh key: ${state.closestItemToPosition(anchorPosition)?.id.toString()}")
            state.closestItemToPosition(anchorPosition)?.id.toString()
        }
    }
}