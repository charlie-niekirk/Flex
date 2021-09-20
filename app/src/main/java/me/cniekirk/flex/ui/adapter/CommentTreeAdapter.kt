package me.cniekirk.flex.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.databinding.SubmissionCommentListItemBinding
import me.cniekirk.flex.util.getDepthColour
import timber.log.Timber

class CommentTreeAdapter(
    private val submission: AuthedSubmission,
    private val items: List<Comment>,
    private val markwon: Markwon) : RecyclerView.Adapter<CommentTreeAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(SubmissionCommentListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) = holder.bind(items[position])
    override fun getItemCount() = items.size

    inner class CommentViewHolder(private val binding: SubmissionCommentListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Comment) {
            binding.apply {
                if (item.depth == 0) {
                    commentDepthIndicator.visibility = View.GONE
                } else {
                    commentDepthIndicator.visibility = View.VISIBLE
                    commentDepthIndicator.setBackgroundColor(root.context.getColor(item.depth.getDepthColour()))
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(root)
                    constraintSet.connect(
                        commentDepthIndicator.id,
                        ConstraintSet.START,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.START,
                        (root.context.resources.getDimension(R.dimen.spacing_m) * item.depth).toInt())
                    constraintSet.connect(
                        itemDivider.id,
                        ConstraintSet.START,
                        commentDepthIndicator.id,
                        ConstraintSet.END,
                        root.context.resources.getDimension(R.dimen.spacing_m).toInt()
                    )
                    constraintSet.applyTo(root)
                }
                markwon.setMarkdown(commentContent, item.body)
                commentAuthorUsername.text = item.author
                if (item.author.equals(submission.author, true)) {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.blue))
                } else {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.accent))
                }
                commentUpvoteNumber.text = item.score.toString()
            }
        }

    }
}