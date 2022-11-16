package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import me.cniekirk.flex.R
import me.cniekirk.flex.data.Cause
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.WorkerRepository
import me.cniekirk.flex.domain.usecase.DownvoteThingUseCase
import me.cniekirk.flex.domain.usecase.GetThingInfoUseCase
import me.cniekirk.flex.domain.usecase.RemoveVoteThingUseCase
import me.cniekirk.flex.domain.usecase.UpvoteThingUseCase
import me.cniekirk.flex.ui.submission.model.UiSubmission
import me.cniekirk.flex.ui.submission.state.*
import me.cniekirk.flex.worker.ScheduledNotificationWorker
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SubmissionActionsViewModel @Inject constructor(
    private val upvoteThingUseCase: UpvoteThingUseCase,
    private val removeVoteThingUseCase: RemoveVoteThingUseCase,
    private val downvoteThingUseCase: DownvoteThingUseCase,
    private val getThingInfoUseCase: GetThingInfoUseCase,
    private val workerRepository: WorkerRepository
) : ViewModel(), ContainerHost<SubmissionActionsState, SubmissionActionsEffect> {

    override val container = container<SubmissionActionsState, SubmissionActionsEffect>(
        SubmissionActionsState()
    ) {

    }

    fun submissionUpdated(uiSubmission: UiSubmission) = intent {
        getThingInfoUseCase(uiSubmission.submissionName).collect { result ->
            when (result) {
                is RedditResult.Error -> {
                    val message = when (result.cause) {
                        Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                        Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                        Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                    }
                    postSideEffect(SubmissionActionsEffect.Error(message))
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    val voteState = result.data.likes?.let { hasUpvoted ->
                        if (hasUpvoted) {
                            VoteState.Upvote
                        } else {
                            VoteState.Downvote
                        }
                    } ?: run {
                        VoteState.NoVote
                    }
                    reduce { state.copy(voteState = voteState) }
                }
            }
        }
    }

    fun upvote(uiSubmission: UiSubmission) = intent {
        when (state.voteState) {
            VoteState.Upvote -> {
                removeVoteThingUseCase(uiSubmission.submissionName)
                    .collect { result ->
                        when (result) {
                            is RedditResult.Error -> {
                                val message = when (result.cause) {
                                    Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                    Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                    Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                                }
                                postSideEffect(SubmissionActionsEffect.Error(message))
                            }
                            RedditResult.Loading -> {}
                            is RedditResult.Success -> {
                                reduce { state.copy(voteState = VoteState.NoVote) }
                            }
                        }
                    }
            }
            VoteState.NoVote, VoteState.Downvote -> {
                upvoteThingUseCase(uiSubmission.submissionName)
                    .collect { result ->
                        when (result) {
                            is RedditResult.Error -> {
                                val message = when (result.cause) {
                                    Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                    Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                    Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                                }
                                postSideEffect(SubmissionActionsEffect.Error(message))
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

    fun downvote(uiSubmission: UiSubmission) = intent {
        when (state.voteState) {
            VoteState.Downvote -> {
                removeVoteThingUseCase(uiSubmission.submissionName)
                    .collect { result ->
                        when (result) {
                            is RedditResult.Error -> {
                                val message = when (result.cause) {
                                    Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                    Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                    Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                                }
                                postSideEffect(SubmissionActionsEffect.Error(message))
                            }
                            RedditResult.Loading -> {}
                            is RedditResult.Success -> {
                                reduce { state.copy(voteState = VoteState.NoVote) }
                            }
                        }
                    }
            }
            VoteState.NoVote, VoteState.Upvote -> {
                downvoteThingUseCase(uiSubmission.submissionName)
                    .collect { result ->
                        when (result) {
                            is RedditResult.Error -> {
                                val message = when (result.cause) {
                                    Cause.NetworkError, Cause.ServerError, Cause.NotFound, Cause.NoConnection -> { R.string.generic_network_error }
                                    Cause.Unauthenticated -> { R.string.action_error_aunauthenticated }
                                    Cause.Unknown, Cause.InsufficientStorage -> { R.string.unknown_error }
                                }
                                postSideEffect(SubmissionActionsEffect.Error(message))
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

    fun postReminderSet(uiSubmission: UiSubmission) = intent {
        val request = OneTimeWorkRequestBuilder<ScheduledNotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .setInputData(workDataOf("THING_ID" to uiSubmission.submissionName))
            .build()
        workerRepository.scheduleOneTimeWork(request)
        postSideEffect(SubmissionActionsEffect.SubmissionReminderSet)
    }
}