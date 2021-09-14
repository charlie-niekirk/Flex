package me.cniekirk.flex.data.remote.pagination

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.cniekirk.flex.data.remote.RedditApi
import me.cniekirk.flex.data.remote.model.RedditType
import timber.log.Timber

class SubmissionCommentsPagingSource(
    private val redditApi: RedditApi,
    private val listingId: String,
    private val sortType: String
) : PagingSource<String, RedditType.T1>() {

    private var before: String? = null
    private var after: String? = null
    private var itemCount: Int = 0

    override suspend fun load(params: PagingSource.LoadParams<String>): PagingSource.LoadResult<String, RedditType.T1> {
        return try {
            val response = if (params.key.equals(before, true)) {
                if (itemCount < 10) {
                    // First page
                    redditApi.getCommentsForListing(listingId, sortType, count = itemCount)
                } else {
                    redditApi.getCommentsForListing(listingId, sortType, count = itemCount, before = params.key)
                }
            } else {
                redditApi.getCommentsForListing(listingId, sortType, after = params.key, count = itemCount)
            }

            if (itemCount > 0 && params.key.equals(before, true)) {
                itemCount -= response.data.dist
            } else {
                itemCount += response.data.dist
            }

            after = response.data.after
            before = response.data.before

            //response.data.children.forEach { (it.data as RedditType.T1).replies }

            LoadResult.Page(
                response.data.children.map { it.data },
                before,
                after)
        } catch (exception: Exception) {
            Timber.e(exception)
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<String, RedditType.T1>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestItemToPosition(anchorPosition)?.id.toString()
        }
    }
}