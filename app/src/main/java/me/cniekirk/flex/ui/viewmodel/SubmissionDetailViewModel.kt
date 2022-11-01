package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.data.Cause
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import me.cniekirk.flex.domain.model.MoreCommentsRequest
import me.cniekirk.flex.domain.usecase.*
import me.cniekirk.flex.ui.submission.SubmissionDetailEvent
import me.cniekirk.flex.ui.submission.state.*
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SubmissionDetailViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getMoreCommentsUseCase: GetMoreCommentsUseCase,
    private val upvoteThingUseCase: UpvoteThingUseCase,
    private val removeVoteThingUseCase: RemoveVoteThingUseCase,
    private val downvoteThingUseCase: DownvoteThingUseCase
) : ViewModel(), ContainerHost<SubmissionDetailState, SubmissionDetailEffect> {

    override val container = container<SubmissionDetailState, SubmissionDetailEffect>(
        SubmissionDetailState()
    )

    fun getComments(submissionId: String, sortType: String) = intent {
        viewModelScope.launch {
            getCommentsUseCase(CommentRequest(submissionId, sortType))
                .collect { response ->
                    when (response) {
                        is RedditResult.Error -> {
                            val message = when (response.cause) {
                                Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                            }
                            postSideEffect(SubmissionDetailEffect.ShowError(message))
                        }
                        RedditResult.Loading -> {
                            // Show loading
                        }
                        is RedditResult.Success -> {
                            val uiComments = response.data.mapNotNull { it.toUiComment() }
                            reduce { state.copy(comments = uiComments) }
                        }
                    }
                }
        }
    }

//    fun getMoreComments(moreComments: MoreComments, parentId: String) = intent {
//        viewModelScope.launch {
//            getMoreCommentsUseCase(MoreCommentsRequest(moreComments, parentId))
//                .collect { commentsTree ->
//                    when (commentsTree) {
//                        is RedditResult.Error -> {
//                            val message = when (commentsTree.cause) {
//                                Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
//                                Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
//                                Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
//                            }
//                            postSideEffect(SubmissionDetailEffect.ShowError(message))
//                        }
//                        is RedditResult.Success -> {
//                            val existing = state.comments
//                            val newComments = mutableListOf<CommentData>()
//                            newComments.addAll(existing)
//                            val replaceIndex = existing.indexOf(moreComments)
//                            newComments.removeAt(replaceIndex)
//                            newComments.addAll(replaceIndex, commentsTree.data)
//                            reduce { state.copy(comments = newComments) }
//                        }
//                        RedditResult.Loading -> {}
//                    }
//                }
//        }
//    }

    fun collapseComment(comment: UiComment) = intent {
        when (comment) {
            is UiComment.Comment -> {
                val currentComments = state.comments
                val startIndex = currentComments.indexOf(comment) + 1
                val endIndex = currentComments.indexOf(
                    currentComments
                        .filter { it.depth <= comment.depth && currentComments.indexOf(it) > startIndex }
                        .minBy { currentComments.indexOf(it) }
                )

                // Map and mutate
                val newList = currentComments.mapIndexed { index, comment ->
                    when (comment) {
                        is UiComment.Comment -> {
                            if (index in startIndex until endIndex) {
                                comment.copy(isCollapsed = !comment.isCollapsed)
                            } else if (index == startIndex - 1) {
                                comment.copy(parentCollapsed = !comment.parentCollapsed)
                            } else {
                                comment.copy()
                            }
                        }
                        is UiComment.MoreComments -> {
                            if (index in startIndex until endIndex) {
                                comment.copy(isCollapsed = !comment.isCollapsed)
                            } else if (index == startIndex - 1) {
                                comment.copy()
                            } else {
                                comment.copy()
                            }
                        }
                    }
                }

                reduce { state.copy(comments = newList) }
            }
            is UiComment.MoreComments -> {
                // TODO: Not yet required
            }
        }
    }

    fun upvoteClicked(thingId: String) = intent {
        when (state.voteState) {
            VoteState.Upvote -> {
                removeVoteThingUseCase(thingId)
                    .collect { result ->
                        when (result) {
                            is RedditResult.Error -> {
                                val message = when (result.cause) {
                                    Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                    Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                    Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                                }
                                postSideEffect(SubmissionDetailEffect.ShowError(message))
                            }
                            RedditResult.Loading -> {}
                            is RedditResult.Success -> {
                                reduce { state.copy(voteState = VoteState.NoVote) }
                            }
                        }
                    }
            }
            VoteState.NoVote, VoteState.Downvote -> {
                upvoteThingUseCase(thingId)
                    .collect { result ->
                        when (result) {
                            is RedditResult.Error -> {
                                val message = when (result.cause) {
                                    Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                    Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                    Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                                }
                                postSideEffect(SubmissionDetailEffect.ShowError(message))
                            }
                            RedditResult.Loading -> {}
                            is RedditResult.Success -> {
                                reduce { state.copy(voteState = VoteState.Upvote) }
                            }
                        }
                    }
            }
        }
    }

    fun downvoteClicked(thingId: String) = intent {
        when (state.voteState) {
            VoteState.Downvote -> {
                removeVoteThingUseCase(thingId)
                    .collect { result ->
                        when (result) {
                            is RedditResult.Error -> {
                                val message = when (result.cause) {
                                    Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                    Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                    Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                                }
                                postSideEffect(SubmissionDetailEffect.ShowError(message))
                            }
                            RedditResult.Loading -> {}
                            is RedditResult.Success -> {
                                reduce { state.copy(voteState = VoteState.NoVote) }
                            }
                        }
                    }
            }
            VoteState.NoVote, VoteState.Upvote -> {
                downvoteThingUseCase(thingId)
                    .collect { result ->
                        when (result) {
                            is RedditResult.Error -> {
                                val message = when (result.cause) {
                                    Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                    Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                    Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                                }
                                postSideEffect(SubmissionDetailEffect.ShowError(message))
                            }
                            RedditResult.Loading -> {}
                            is RedditResult.Success -> {
                                reduce { state.copy(voteState = VoteState.Downvote) }
                            }
                        }
                    }
            }
        }
    }
}