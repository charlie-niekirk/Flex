package me.cniekirk.flex.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.noties.markwon.Markwon
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.data.remote.model.Comment
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.databinding.SubmissionCommentCollapsedListItemBinding
import me.cniekirk.flex.databinding.SubmissionCommentListItemBinding
import me.cniekirk.flex.util.getDepthColour
import timber.log.Timber

enum class CommentViewType {
    COMMENT,
    COLLAPSED_COMMENT
}

class CommentTreeAdapter(
    private val submission: AuthedSubmission,
    private val items: MutableList<Comment>,
    private val markwon: Markwon) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val currentItems = items

    override fun getItemViewType(position: Int): Int {
        return when (items[position].isCollapsed) {
            true -> { CommentViewType.COLLAPSED_COMMENT.ordinal }
            false -> { CommentViewType.COMMENT.ordinal }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CommentViewType.COMMENT.ordinal -> {
                CommentViewHolder(SubmissionCommentListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false))
            }
            CommentViewType.COLLAPSED_COMMENT.ordinal -> {
                CommentCollapsedViewHolder(
                    SubmissionCommentCollapsedListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false))
            }
            else -> {
                CommentViewHolder(SubmissionCommentListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (items[position].isCollapsed) {
            true -> {
                (holder as CommentCollapsedViewHolder).bind(items[position])
            }
            false -> {
                (holder as CommentViewHolder).bind(items[position])
            }
        }
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
                val topAwards = item.allAwarding?.sortedByDescending { it.coinPrice }?.take(3)
                if (!topAwards.isNullOrEmpty()) {
                    binding.awards.textTotalAwardCount.visibility = View.VISIBLE
                    binding.awards.textTotalAwardCount.text = topAwards.sumOf { it.count ?: 0 }.toString()
                    when (topAwards.size) {
                        1 -> {
                            binding.awards.imageSecondAward.visibility = View.GONE
                            binding.awards.imageThirdAward.visibility = View.GONE
                            Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                        }
                        2 -> {
                            binding.awards.imageSecondAward.visibility = View.VISIBLE
                            binding.awards.imageThirdAward.visibility = View.GONE
                            Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                            Glide.with(binding.root).load(topAwards[1].iconUrl).into(binding.awards.imageSecondAward)
                        }
                        else -> {
                            binding.awards.imageSecondAward.visibility = View.VISIBLE
                            binding.awards.imageThirdAward.visibility = View.VISIBLE
                            Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                            Glide.with(binding.root).load(topAwards[1].iconUrl).into(binding.awards.imageSecondAward)
                            Glide.with(binding.root).load(topAwards[2].iconUrl).into(binding.awards.imageThirdAward)
                        }
                    }
                } else {
                    binding.awards.imageFirstAward.visibility = View.GONE
                    binding.awards.imageSecondAward.visibility = View.GONE
                    binding.awards.imageThirdAward.visibility = View.GONE
                    binding.awards.textTotalAwardCount.visibility = View.GONE
                }
                root.setOnClickListener {
                    if (item.isCollapsed) {
                        val newList = mutableListOf<Comment>()
                        expandComment(item, newList, 0)
                        items.addAll(items.indexOf(item) + 1, newList)
                        notifyItemRangeInserted(items.indexOf(item) + 1, newList.size)
                    } else {
                        collapseComment(item)
                    }
                }
            }
        }
    }

    inner class CommentCollapsedViewHolder(private val binding: SubmissionCommentCollapsedListItemBinding)
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
                        (root.context.resources.getDimension(R.dimen.spacing_m) * item.depth).toInt()
                    )
                    constraintSet.connect(
                        itemDivider.id,
                        ConstraintSet.START,
                        commentDepthIndicator.id,
                        ConstraintSet.END,
                        root.context.resources.getDimension(R.dimen.spacing_m).toInt()
                    )
                    constraintSet.applyTo(root)
                }
                commentAuthorUsername.text = item.author
                if (item.author.equals(submission.author, true)) {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.blue))
                } else {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.accent))
                }
                val children = mutableListOf<Comment>()
                createChildList(item, children, 0)
                if (children.size > 0) {
                    commentChildrenNumber.text = children.size.toString()
                } else {
                    commentChildrenNumber.text = ""
                }
                commentUpvoteNumber.text = item.score.toString()
                val topAwards = item.allAwarding?.sortedByDescending { it.coinPrice }?.take(3)
                if (!topAwards.isNullOrEmpty()) {
                    binding.awards.textTotalAwardCount.visibility = View.VISIBLE
                    binding.awards.textTotalAwardCount.text = topAwards.sumOf { it.count ?: 0 }.toString()
                    when (topAwards.size) {
                        1 -> {
                            binding.awards.imageSecondAward.visibility = View.GONE
                            binding.awards.imageThirdAward.visibility = View.GONE
                            Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                        }
                        2 -> {
                            binding.awards.imageSecondAward.visibility = View.VISIBLE
                            binding.awards.imageThirdAward.visibility = View.GONE
                            Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                            Glide.with(binding.root).load(topAwards[1].iconUrl).into(binding.awards.imageSecondAward)
                        }
                        else -> {
                            binding.awards.imageSecondAward.visibility = View.VISIBLE
                            binding.awards.imageThirdAward.visibility = View.VISIBLE
                            Glide.with(binding.root).load(topAwards[0].iconUrl).into(binding.awards.imageFirstAward)
                            Glide.with(binding.root).load(topAwards[1].iconUrl).into(binding.awards.imageSecondAward)
                            Glide.with(binding.root).load(topAwards[2].iconUrl).into(binding.awards.imageThirdAward)
                        }
                    }
                } else {
                    binding.awards.imageFirstAward.visibility = View.GONE
                    binding.awards.imageSecondAward.visibility = View.GONE
                    binding.awards.imageThirdAward.visibility = View.GONE
                    binding.awards.textTotalAwardCount.visibility = View.GONE
                }
                root.setOnClickListener {
                    if (item.isCollapsed) {
                        // Just expand this comment if there are no children
                        if (item.replies.isNullOrEmpty()) {
                            item.isCollapsed = false
                            notifyItemChanged(items.indexOf(item))
                        } else {
                            val newList = mutableListOf<Comment>()
                            expandComment(item, newList, 0)
                            items.addAll(items.indexOf(item) + 1, newList)
                            notifyItemRangeInserted(items.indexOf(item) + 1, newList.size)
                        }
                    } else {
                        collapseComment(item)
                    }
                }
            }
        }
    }

    /**
     * Gets all child comments recursively and sets them to be expanded
     *
     * @param comment the comment
     */
    private fun expandComment(comment: Comment, expandedChildren: MutableList<Comment>, position: Int) {
        comment.isCollapsed = false
        val children = comment.replies?.map { it as Comment }
        expandedChildren.addAll(position, children!!)
        var pos = position
        children.forEach {
            pos = pos.inc()
            if (!it.replies.isNullOrEmpty()) {
                expandComment(it, expandedChildren, pos)
                val temp = mutableListOf<Comment>()
                createChildList(it, temp, 0)
                pos += temp.size
            }
        }
    }

    /**
     * Collapses all child comments and changes the top-level comment to reflect the
     * collapsed state
     *
     * @param comment the top level comment, the children of which will all be collapsed
     */
    private fun collapseComment(comment: Comment) {
        comment.isCollapsed = true
        val children = getNumChildren(comment)
        items.subList(items.indexOf(comment) + 1, items.indexOf(comment) + 1 + children).clear()
        notifyItemRangeRemoved(items.indexOf(comment) + 1, children)
        notifyItemChanged(items.indexOf(comment))
    }

    private fun getNumChildren(comment: Comment): Int {
        var children = 0
        for (i in (items.indexOf(comment) + 1)..items.lastIndex) {
            if (items[i].depth > comment.depth) {
                children += 1
            } else {
                break
            }
        }
        return children
    }

    private fun createChildList(comment: Comment, childList: MutableList<Comment>, position: Int) {
        val children = comment.replies?.map { it as Comment }
        children?.let { childList.addAll(children) }
        var pos = position
        children?.forEach {
            pos = pos.inc()
            if (!it.replies.isNullOrEmpty()) {
                expandComment(it, childList, pos)
            }
        }
    }
}