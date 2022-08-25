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
import me.cniekirk.flex.ui.submission.state.SubmissionDetailEffect
import me.cniekirk.flex.ui.submission.state.SubmissionDetailState
import me.cniekirk.flex.ui.submission.state.VoteState
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
                            reduce { state.copy(comments = response.data) }
                        }
                    }
                }
        }
    }

    fun getMoreComments(moreComments: MoreComments, parentId: String) = intent {
        viewModelScope.launch {
            getMoreCommentsUseCase(MoreCommentsRequest(moreComments, parentId))
                .collect { commentsTree ->
                    when (commentsTree) {
                        is RedditResult.Error -> {
                            val message = when (commentsTree.cause) {
                                Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                            }
                            postSideEffect(SubmissionDetailEffect.ShowError(message))
                        }
                        is RedditResult.Success -> {
                            val existing = state.comments
                            val newComments = mutableListOf<CommentData>()
                            newComments.addAll(existing)
                            val replaceIndex = existing.indexOf(moreComments)
                            newComments.removeAt(replaceIndex)
                            newComments.addAll(replaceIndex, commentsTree.data)
                            reduce { state.copy(comments = newComments) }
                        }
                        RedditResult.Loading -> {}
                    }
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