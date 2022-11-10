package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import me.cniekirk.flex.R
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.usecase.DownvoteThingUseCase
import me.cniekirk.flex.domain.usecase.RemoveVoteThingUseCase
import me.cniekirk.flex.domain.usecase.UpvoteThingUseCase
import me.cniekirk.flex.ui.submission.model.UiSubmission
import me.cniekirk.flex.ui.submission.state.SubmissionActionsEffect
import me.cniekirk.flex.ui.submission.state.SubmissionActionsState
import me.cniekirk.flex.ui.submission.state.VoteState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class SubmissionActionsViewModel @Inject constructor(
    private val upvoteThingUseCase: UpvoteThingUseCase,
    private val removeVoteThingUseCase: RemoveVoteThingUseCase,
    private val downvoteThingUseCase: DownvoteThingUseCase
) : ViewModel(), ContainerHost<SubmissionActionsState, SubmissionActionsEffect> {

    override val container = container<SubmissionActionsState, SubmissionActionsEffect>(
        SubmissionActionsState()
    )

    fun upvote(uiSubmission: UiSubmission) = intent {
        upvoteThingUseCase(uiSubmission.submissionName).collect { response ->
            when (response) {
                is RedditResult.Error -> {
                    postSideEffect(SubmissionActionsEffect.Error(R.string.generic_network_error))
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    reduce { state.copy(voteState = VoteState.Upvote) }
                    postSideEffect(SubmissionActionsEffect.ActionCompleted(R.string.upvote_success))
                }
            }
        }
    }

    fun removeVote(uiSubmission: UiSubmission) = intent {
        removeVoteThingUseCase(uiSubmission.submissionName).collect { response ->
            when (response) {
                is RedditResult.Error -> {
                    postSideEffect(SubmissionActionsEffect.Error(R.string.generic_network_error))
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    reduce { state.copy(voteState = VoteState.NoVote) }
                    postSideEffect(SubmissionActionsEffect.ActionCompleted(R.string.remove_vote_success))
                }
            }
        }
    }

    fun downvote(uiSubmission: UiSubmission) = intent {
        downvoteThingUseCase(uiSubmission.submissionName).collect { response ->
            when (response) {
                is RedditResult.Error -> {
                    postSideEffect(SubmissionActionsEffect.Error(R.string.generic_network_error))
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    reduce { state.copy(voteState = VoteState.Downvote) }
                    postSideEffect(SubmissionActionsEffect.ActionCompleted(R.string.downvote_success))
                }
            }
        }
    }
}