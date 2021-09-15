package me.cniekirk.flex.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.databinding.SubmissionCommentListItemBinding
import timber.log.Timber

class CommentTreeAdapter(
    private val items: List<Comment>,
    private val markwon: Markwon) : RecyclerView.Adapter<CommentTreeAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(SubmissionCommentListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        ))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class CommentViewHolder(private val binding: SubmissionCommentListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Comment) {
            binding.apply {
                if (item.depth == 0) {
                    commentDepthIndicator.visibility = View.GONE
                } else {

                    commentDepthIndicator.visibility = View.VISIBLE
                }
                markwon.setMarkdown(commentContent, item.body)
                commentAuthorUsername.text = item.author
                commentUpvoteNumber.text = item.score.toString()
            }
        }

    }
}