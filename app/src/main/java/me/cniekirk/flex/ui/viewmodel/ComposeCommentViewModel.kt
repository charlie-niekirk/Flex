package me.cniekirk.flex.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.cniekirk.flex.data.remote.model.imgur.Data
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.domain.model.CommentParams
import me.cniekirk.flex.domain.usecase.SubmitCommentUseCase
import me.cniekirk.flex.domain.usecase.UploadImgurImageUseCase
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class ComposeCommentViewModel @Inject constructor(
    private val uploadImgurImageUseCase: UploadImgurImageUseCase,
    private val submitCommentUseCase: SubmitCommentUseCase
) : ViewModel() {

    private val _imageResponse = MutableSharedFlow<RedditResult<ImgurResponse<Data>>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val imageResponse: Flow<RedditResult<ImgurResponse<Data>>> = _imageResponse.distinctUntilChanged()

    private val _submitCommentResponse = MutableSharedFlow<RedditResult<CommentData>>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val submitCommentResponse: Flow<RedditResult<CommentData>> = _submitCommentResponse.distinctUntilChanged()

    fun uploadImage(image: RequestBody) {
        viewModelScope.launch {
            val imageRequestBody = MultipartBody.Part.createFormData("image", "post_image", image)
            _imageResponse.emitAll(uploadImgurImageUseCase(imageRequestBody))
        }
    }

    fun submitComment(content: String, parentThing: String) {
        viewModelScope.launch {
            _submitCommentResponse.emitAll(submitCommentUseCase(CommentParams(content, parentThing)))
        }
    }
}