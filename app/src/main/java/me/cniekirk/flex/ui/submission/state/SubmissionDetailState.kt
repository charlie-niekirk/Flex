package me.cniekirk.flex.ui.submission.state

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.Cause
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.MoreComments

data class SubmissionDetailState(
    val comments: List<UiComment> = emptyList(),
    val voteState: VoteState = VoteState.NoVote
)

@Parcelize
sealed class VoteState : Parcelable {
    object Upvote : VoteState()
    object Downvote : VoteState()
    object NoVote : VoteState()
}

sealed class UiComment {

    abstract val depth: Int
    abstract val isCollapsed: Boolean

    data class Comment(
        val body: String,
        val author: String,
        override val depth: Int,
        override val isCollapsed: Boolean,
        val parentCollapsed: Boolean
    ) : UiComment()

    data class MoreComments(
        val count: Int,
        override val depth: Int,
        override val isCollapsed: Boolean
    ) : UiComment()
}

fun CommentData.toUiComment() : UiComment? {
    return when (this) {
        is Comment -> {
            UiComment.Comment(
                this.body ?: "",
                this.author ?: "",
                this.depth,
                this.isCollapsed,
                false
            )
        }
        is MoreComments -> {
            UiComment.MoreComments(
                this.count,
                this.depth,
                this.isCollapsed
            )
        }
        else -> null
    }
}

//fun List<CommentData>?.getFullChildSize(starting: Int = 0): Int {
//    return if (this != null && this.isNotEmpty()) {
//        starting + this.getFullChildSize(this.size)
//    } else {
//        0
//    }
//}

sealed class SubmissionDetailEffect {
    data class ShowError(@StringRes val message: Int) : SubmissionDetailEffect()
}