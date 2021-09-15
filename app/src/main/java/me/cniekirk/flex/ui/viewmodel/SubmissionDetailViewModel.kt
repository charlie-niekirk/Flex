package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.Data
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.domain.GetCommentsUseCase
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentRequest
import javax.inject.Inject

@HiltViewModel
class SubmissionDetailViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase
) : ViewModel() {

    private val _commentsTree: MutableStateFlow<RedditResult<List<Comment>>> = MutableStateFlow(RedditResult.Loading)
    val commentsTree: StateFlow<RedditResult<List<Comment>>> = _commentsTree

    fun getComments(submissionId: String, sortType: String) {
        viewModelScope.launch {
            getCommentsUseCase(CommentRequest(submissionId, sortType))
                .collect { comments -> _commentsTree.value = comments }
        }
    }

}