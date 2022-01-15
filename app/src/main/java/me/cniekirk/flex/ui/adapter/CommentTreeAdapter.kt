package me.cniekirk.flex.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.noties.markwon.Markwon
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.databinding.SubmissionCommentCollapsedListItemBinding
import me.cniekirk.flex.databinding.SubmissionCommentListItemBinding
import me.cniekirk.flex.databinding.SubmissionCommentLoadMoreListItemBinding
import me.cniekirk.flex.ui.gallery.SlidingGalleryContainer
import me.cniekirk.flex.util.ContentLink
import me.cniekirk.flex.util.getDepthColour
import me.cniekirk.flex.util.resolveColorAttr
import me.cniekirk.flex.util.toYtThumb

enum class CommentViewType {
    COMMENT,
    COLLAPSED_COMMENT,
    MORE_COMMENTS
}

class CommentTreeAdapter(
    private val submission: AuthedSubmission,
    private val markwon: Markwon,
    private val commentActionListener: CommentActionListener) : ListAdapter<CommentData, RecyclerView.ViewHolder>(CommentDiff) {

    override fun getItemViewType(position: Int): Int {
        return when (currentList[position]) {
            is MoreComments -> { CommentViewType.MORE_COMMENTS.ordinal }
            is Comment -> {
                val comment = currentList[position] as Comment
                if (comment.isCollapsed) {
                    CommentViewType.COLLAPSED_COMMENT.ordinal
                } else {
                    CommentViewType.COMMENT.ordinal
                }
            }
            else -> { CommentViewType.COMMENT.ordinal }
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
            CommentViewType.MORE_COMMENTS.ordinal -> {
                MoreCommentsViewHolder(
                    SubmissionCommentLoadMoreListItemBinding.inflate(
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
        when (getItemViewType(position)) {
            CommentViewType.COLLAPSED_COMMENT.ordinal -> {
                (holder as CommentCollapsedViewHolder).bind(currentList[position] as Comment)
            }
            CommentViewType.COMMENT.ordinal -> {
                (holder as CommentViewHolder).bind(currentList[position] as Comment)
            }
            CommentViewType.MORE_COMMENTS.ordinal -> {
                (holder as MoreCommentsViewHolder).bind(currentList[position] as MoreComments)
            }
        }
    }

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
                        ConstraintSet.START,
                        root.context.resources.getDimension(R.dimen.spacing_m).toInt()
                    )
                    constraintSet.applyTo(root)
                }
                markwon.setMarkdown(commentContent, item.body ?: "")
                commentAuthorUsername.text = item.author
                if (item.author.equals(submission.author, true)) {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.blue))
                } else if (item.distinguishedRaw.equals("moderator", true)) {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.green))
                } else if (item.distinguishedRaw.equals("admin", true)) {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.red))
                } else {
                    commentAuthorUsername.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
                }
                commentLocked.isGone = !item.isLocked!!
                commentPinned.isGone = !item.isStickied!!
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
                        val newList = mutableListOf<CommentData>()
                        expandComment(item, newList, 0)
                        val expanded = currentList.toMutableList().apply { addAll(currentList.indexOf(item) + 1, newList) }
                        expanded[expanded.indexOf(item)] = (expanded[expanded.indexOf(item)] as Comment).copy(isCollapsed = false)
                        submitList(expanded)
                    } else {
                        collapseComment(item)
                    }
                }
                commentReplyButton.setOnClickListener {
                    if (!item.isCollapsed) {
                        commentActionListener.onReply(item)
                    }
                }
                parsedLinksLayout.isVisible = !item.contentLinks.isNullOrEmpty()
                if (item.contentLinks?.size != parsedLinksLayout.childCount) {
                    item.contentLinks?.forEachIndexed { index, link ->
                        val view = when (link) {
                            is ContentLink.ImageLink -> {
                                val temp = LayoutInflater.from(parsedLinksLayout.context).inflate(
                                    R.layout.image_link_preview_view, parsedLinksLayout, true)
                                (temp as LinearLayout)[index].findViewById<TextView>(R.id.link_text).text = link.url
                                temp
                            }
                            is ContentLink.ImgurGalleryLink -> {
                                val temp = LayoutInflater.from(parsedLinksLayout.context).inflate(
                                    R.layout.image_link_preview_view, parsedLinksLayout, true)
                                (temp as LinearLayout)[index].findViewById<TextView>(R.id.link_text).text = link.url
                                temp.setOnClickListener {
                                    commentActionListener.onImgurGalleryClicked(link.url)
                                }
                                temp
                            }
                            is ContentLink.ImgurImageLink -> {
                                val temp = LayoutInflater.from(parsedLinksLayout.context).inflate(
                                    R.layout.image_link_preview_view, parsedLinksLayout, true)
                                (temp as LinearLayout)[index].findViewById<TextView>(R.id.link_text).text = link.url
                                temp
                            }
                            is ContentLink.VideoLink -> {
                                val temp = LayoutInflater.from(parsedLinksLayout.context).inflate(
                                    R.layout.image_link_preview_view, parsedLinksLayout, true)
                                (temp as LinearLayout)[index].findViewById<TextView>(R.id.link_text).text = link.url
                                temp
                            }
                            is ContentLink.WebLink -> {
                                val temp = LayoutInflater.from(parsedLinksLayout.context).inflate(
                                    R.layout.web_link_preview_view, parsedLinksLayout, true)
                                (temp as LinearLayout)[index].findViewById<TextView>(R.id.link_text).text = link.url
                                temp
                            }
                            is ContentLink.WikipediaLink -> {
                                val temp = LayoutInflater.from(parsedLinksLayout.context).inflate(
                                    R.layout.wikipedia_link_preview_view, parsedLinksLayout, true)
                                (temp as LinearLayout)[index].findViewById<TextView>(R.id.title_text).text = link.title
                                temp[index].findViewById<TextView>(R.id.summary_text).text = link.summary
                                temp
                            }
                            is ContentLink.YoutubeLink -> {
                                val temp = LayoutInflater.from(parsedLinksLayout.context).inflate(
                                    R.layout.youtube_link_preview_view, parsedLinksLayout, true)
                                (temp as LinearLayout)[index].findViewById<ImageView>(R.id.image_preview).apply {
                                    val thumb = if (link.url.contains("youtu.be")) {
                                        link.url.substringAfterLast("/").toYtThumb()
                                    } else {
                                        link.url.substringAfter("=").toYtThumb()
                                    }
                                    Glide.with(this).load(thumb).into(this)
                                }
                                (temp)[index].findViewById<TextView>(R.id.link_text).text = link.url
                                temp
                            }
                        }
                        if (!view.hasOnClickListeners()) {
                            view.setOnClickListener {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    data = Uri.parse(link.url)
                                }
                                root.context.startActivity(intent)
                            }
                        }
                    }
                }
                if (item.contentLinks.isNullOrEmpty()) {
                    val cs = ConstraintSet()
                    cs.clone(root)
                    cs.connect(commentContent.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM,
                        root.context.resources.getDimensionPixelSize(R.dimen.spacing_l))
                    cs.applyTo(root)
                } else {
                    val cs = ConstraintSet()
                    cs.clone(root)
                    cs.connect(commentContent.id, ConstraintSet.BOTTOM, parsedLinksLayout.id, ConstraintSet.TOP, 0)
                    cs.applyTo(root)
                }
            }
        }
    }

    /**
     * [RecyclerView.ViewHolder] that displays a collapsed comment in a comment tree, which can then
     * be expanded by the user
     *
     * @param
     */
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
                } else if (item.distinguishedRaw.equals("moderator", true)) {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.green))
                } else if (item.distinguishedRaw.equals("admin", true)) {
                    commentAuthorUsername.setTextColor(root.context.getColor(R.color.red))
                } else {
                    commentAuthorUsername.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
                }
                commentLocked.isGone = !item.isLocked!!
                commentPinned.isGone = !item.isStickied!!
                val children = mutableListOf<CommentData>()
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
                        val newList = mutableListOf<CommentData>()
                        expandComment(item, newList, 0)
                        val expanded = currentList.toMutableList().apply { addAll(currentList.indexOf(item) + 1, newList) }
                        expanded[expanded.indexOf(item)] = (expanded[expanded.indexOf(item)] as Comment).copy(isCollapsed = false)
                        submitList(expanded)
                    } else {
                        collapseComment(item)
                    }
                }
            }
        }
    }

    /**
     * [RecyclerView.ViewHolder] that displays a more_comments object in a comment tree which can
     * be manually expanded by the user
     *
     * @param binding the view binding instance for this view type
     */
    inner class MoreCommentsViewHolder(private val binding: SubmissionCommentLoadMoreListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(moreComments: MoreComments) {
            binding.apply {
                if (moreComments.depth == 0) {
                    commentDepthIndicator.visibility = View.GONE
                } else {
                    commentDepthIndicator.visibility = View.VISIBLE
                    commentDepthIndicator.setBackgroundColor(root.context.getColor(moreComments.depth.getDepthColour()))
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(root)
                    constraintSet.connect(
                        commentDepthIndicator.id,
                        ConstraintSet.START,
                        ConstraintSet.PARENT_ID,
                        ConstraintSet.START,
                        (root.context.resources.getDimension(R.dimen.spacing_m) * moreComments.depth).toInt()
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
                numMoreReplies.text =
                    binding.root.context.getString(R.string.num_replies, moreComments.count)
                root.setOnClickListener { commentActionListener.onLoadMore(moreComments) }
            }
        }

    }

    /**
     * Gets all child comments recursively and sets them to be expanded
     *
     * @param comment the comment
     */
    private fun expandComment(comment: CommentData, expandedChildren: MutableList<CommentData>, position: Int) {
        val children = comment.replies
        expandedChildren.addAll(position, children ?: emptyList())
        var pos = position
        children?.forEach {
            pos = pos.inc()
            if (!it.replies.isNullOrEmpty()) {
                expandComment(it, expandedChildren, pos)
                val temp = mutableListOf<CommentData>()
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
    private fun collapseComment(comment: CommentData) {
        val children = getNumChildren(comment)
        val idx = currentList.indexOf(comment)
        val cleared = currentList.toMutableList().apply {
            set(idx, (get(idx) as Comment).copy(isCollapsed = true))
            subList(currentList.indexOf(comment) + 1, currentList.indexOf(comment) + 1 + children).apply { clear() }
        }
        submitList(cleared)
    }

    private fun getNumChildren(comment: CommentData): Int {
        var children = 0
        for (i in (currentList.indexOf(comment) + 1)..currentList.lastIndex) {
            if (currentList[i].depth > comment.depth) {
                children += 1
            } else {
                break
            }
        }
        return children
    }

    private fun createChildList(comment: CommentData, childList: MutableList<CommentData>, position: Int) {
        val children = comment.replies
        children?.let { childList.addAll(children) }
        var pos = position
        children?.forEach {
            pos = pos.inc()
            if (!it.replies.isNullOrEmpty()) {
                expandComment(it, childList, pos)
            }
        }
    }

    object CommentDiff : DiffUtil.ItemCallback<CommentData>() {
        override fun areItemsTheSame(oldItem: CommentData, newItem: CommentData) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: CommentData, newItem: CommentData): Boolean {
            return oldItem.isCollapsed == newItem.isCollapsed
        }
    }

    interface CommentActionListener {
        fun onLoadMore(moreComments: MoreComments)
        fun onReply(comment: Comment)
        fun onImgurGalleryClicked(albumId: String)
    }
}