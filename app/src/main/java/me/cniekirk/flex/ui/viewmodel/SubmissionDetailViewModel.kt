package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import me.cniekirk.flex.domain.model.MoreCommentsRequest
import me.cniekirk.flex.domain.usecase.*
import me.cniekirk.flex.ui.submission.SubmissionDetailEvent
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SubmissionDetailViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getMoreCommentsUseCase: GetMoreCommentsUseCase,
    private val upvoteThingUseCase: UpvoteThingUseCase,
    private val removeVoteThingUseCase: RemoveVoteThingUseCase,
    private val downvoteThingUseCase: DownvoteThingUseCase
) : ViewModel() {

    // TODO: ADD TOP PART AS HEADER RV ITEM TO REMOVE JANK

    private val _commentsTree: MutableStateFlow<RedditResult<List<CommentData>>> = MutableStateFlow(RedditResult.Loading)
    val commentsTree: StateFlow<RedditResult<List<CommentData>>> = _commentsTree
    private val _voteState: MutableStateFlow<RedditResult<Boolean>> = MutableStateFlow(RedditResult.Loading)
    val voteState: StateFlow<RedditResult<Boolean>> = _voteState

    fun getComments(submission: AuthedSubmission, sortType: String) {
        viewModelScope.launch {
            getCommentsUseCase(CommentRequest(submission.id, sortType))
                .collect { comments -> _commentsTree.value = comments }
        }
    }

    fun getMoreComments(moreComments: MoreComments, parentId: String) {
        viewModelScope.launch {
            getMoreCommentsUseCase(MoreCommentsRequest(moreComments, parentId))
                .collect { commentsTree ->
                    val existing = _commentsTree.value
                    if (existing is RedditResult.Success && commentsTree is RedditResult.Success) {
                        val comments = existing.data
                        val newComments = mutableListOf<CommentData>()
                        newComments.addAll(comments)
                        val replaceIndex = comments.indexOf(moreComments)
                        newComments.removeAt(replaceIndex)
                        newComments.addAll(replaceIndex, commentsTree.data)
                        _commentsTree.value = RedditResult.Success(newComments)
                    } else if (commentsTree is RedditResult.Error) {
                        Timber.e(commentsTree.errorMessage)
                    }
                }
        }
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