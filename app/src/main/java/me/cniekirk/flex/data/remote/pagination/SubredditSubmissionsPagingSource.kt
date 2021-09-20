package me.cniekirk.flex.data.remote.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.cniekirk.flex.data.local.db.UserDao
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.data.remote.model.Submission
import timber.log.Timber
import javax.inject.Named

class SubredditSubmissionsPagingSource(
    private val redditApi: RedditApi,
    private val authRedditApi: RedditApi,
    private val subreddit: String,
    private val sortType: String,
    private val userDao: UserDao
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

            if (itemCount > 0 && params.key.equals(before, true)) {
                itemCount -= response.data.dist
            } else {
                itemCount += response.data.dist
            }

            after = response.data.after
            before = response.data.before

            LoadResult.Page(
                response.data?.children?.map { it.data },
                before,
                after)
        } catch (exception: Exception) {
            Timber.e(exception)
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<String, AuthedSubmission>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id.toString()
        }
    }
}