package me.cniekirk.flex.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.base.Listing
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedCommentData
import me.cniekirk.flex.di.IoDispatcher
import me.cniekirk.flex.domain.FlowUseCase
import me.cniekirk.flex.domain.RedditDataRepository
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import me.cniekirk.flex.util.ContentLink
import javax.inject.Inject

/**
 * Use case that fetches comment data from the repository and builds a flattened tree of
 * comments to be rendered by the UI, it isn't concerned with where the data comes from
 * or how it is displayed, it just wraps the business logic
 *
 * @param repository the repository from which to fetch the data
 * @param coroutineDispatcher the dispatcher on which to launch the coroutine
 *
 * @author Charlie Niekirk
 */
class GetCommentsUseCase @Inject constructor(
    private val repository: RedditDataRepository,
    @IoDispatcher coroutineDispatcher: CoroutineDispatcher
) : FlowUseCase<CommentRequest, List<CommentData>>(coroutineDispatcher) {

    @Suppress("UNCHECKED_CAST")
    override suspend fun execute(parameters: CommentRequest): Flow<RedditResult<List<CommentData>>> = flow {
        val response = repository.getComments(parameters.submissionId, parameters.sortType)
        val commentTree = response.lastOrNull()?.data as Listing<EnvelopedCommentData>
        val comments = mutableListOf<CommentData>()
        buildTree(commentTree, comments)

        comments.forEach {
            it.contentLinks?.forEach { link ->
                when (link) {
                    is ContentLink.WikipediaLink -> {
                        when (val wikiSummary = repository.getWikipediaSummary(link.url.substringAfterLast("/"))) {
                            is RedditResult.Success -> {
                                (it.contentLinks?.find { old -> old.url.equals(link.url, true) } as ContentLink.WikipediaLink).apply {
                                    title = wikiSummary.data.title
                                    summary = wikiSummary.data.extract
                                }
                            }
                            else -> {}
                        }
                    }
                    else -> {}
                }
            }
        }

        emit(RedditResult.Success(comments))
    }

    /**
     * Recursive function to flatten the returned data into a structure more usable
     * in the UI layer
     *
     * @param data the data from the repository
     * @param output the output list which is finally exposed to the UI
     */
    private fun buildTree(data: Listing<EnvelopedCommentData>, output: MutableList<CommentData>) {
        data.children.forEach {
            val topLevel = it.data
            output.add(topLevel)
            if (!topLevel.replies.isNullOrEmpty() && topLevel is Comment) {
                buildTree(topLevel.repliesRaw!!.data, output)
            }
        }
    }
}