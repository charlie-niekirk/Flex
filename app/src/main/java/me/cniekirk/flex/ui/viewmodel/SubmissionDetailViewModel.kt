package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.data.remote.model.CommentData
import me.cniekirk.flex.data.remote.model.MoreComments
import me.cniekirk.flex.domain.usecase.GetCommentsUseCase
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import me.cniekirk.flex.domain.usecase.DownvoteThingUseCase
import me.cniekirk.flex.domain.usecase.RemoveVoteThingUseCase
import me.cniekirk.flex.domain.usecase.UpvoteThingUseCase
import me.cniekirk.flex.ui.submission.SubmissionDetailEvent
import javax.inject.Inject

@HiltViewModel
class SubmissionDetailViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val upvoteThingUseCase: UpvoteThingUseCase,
    private val removeVoteThingUseCase: RemoveVoteThingUseCase,
    private val downvoteThingUseCase: DownvoteThingUseCase
) : ViewModel() {

    private val _commentsTree: MutableStateFlow<RedditResult<List<CommentData>>> = MutableStateFlow(RedditResult.Loading)
    val commentsTree: StateFlow<RedditResult<List<CommentData>>> = _commentsTree
    private val _voteState: MutableStateFlow<RedditResult<Boolean>> = MutableStateFlow(RedditResult.Loading)
    val voteState: StateFlow<RedditResult<Boolean>> = _voteState

    fun getComments(submissionId: String, sortType: String) {
        viewModelScope.launch {
            getCommentsUseCase(CommentRequest(submissionId, sortType))
                .collect { comments -> _commentsTree.value = comments }
        }
    }

    fun getMoreComments(moreComments: MoreComments) {

    }

    fun onUiEvent(submissionDetailEvent: SubmissionDetailEvent) {
        viewModelScope.launch {
            when (submissionDetailEvent) {
                is SubmissionDetailEvent.Downvote -> {
                    downvoteThingUseCase(submissionDetailEvent.thingId)
                        .collect { _voteState.value = it }
                }
                is SubmissionDetailEvent.RemoveDownvote -> {}
                is SubmissionDetailEvent.RemoveUpvote -> {
                    removeVoteThingUseCase(submissionDetailEvent.thingId)
                        .collect { _voteState.value = it }
                }
                is SubmissionDetailEvent.Upvote -> {
                    upvoteThingUseCase(submissionDetailEvent.thingId)
                        .collect { _voteState.value = it }
                }
            }
        }
    }

}