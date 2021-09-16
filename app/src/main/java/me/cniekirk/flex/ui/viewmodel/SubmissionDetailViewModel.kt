package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.domain.usecase.GetCommentsUseCase
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import me.cniekirk.flex.domain.usecase.UpvoteThingUseCase
import me.cniekirk.flex.ui.submission.SubmissionDetailEvent
import javax.inject.Inject

@HiltViewModel
class SubmissionDetailViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val upvoteThingUseCase: UpvoteThingUseCase
) : ViewModel() {

    private val _commentsTree: MutableStateFlow<RedditResult<List<Comment>>> = MutableStateFlow(RedditResult.Loading)
    val commentsTree: StateFlow<RedditResult<List<Comment>>> = _commentsTree
    private val _upvoteState: MutableStateFlow<RedditResult<Boolean>> = MutableStateFlow(RedditResult.Loading)
    val upvoteState: StateFlow<RedditResult<Boolean>> = _upvoteState

    fun getComments(submissionId: String, sortType: String) {
        viewModelScope.launch {
            getCommentsUseCase(CommentRequest(submissionId, sortType))
                .collect { comments -> _commentsTree.value = comments }
        }
    }

    fun onUiEvent(submissionDetailEvent: SubmissionDetailEvent) {
        viewModelScope.launch {
            when (submissionDetailEvent) {
                is SubmissionDetailEvent.Downvote -> {}
                is SubmissionDetailEvent.RemoveDownvote -> {}
                is SubmissionDetailEvent.RemoveUpvote -> {}
                is SubmissionDetailEvent.Upvote -> {
                    upvoteThingUseCase(submissionDetailEvent.thingId)
                        .collect { _upvoteState.value = it }
                }
            }
        }
    }

}