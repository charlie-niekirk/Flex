package me.cniekirk.flex.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.marginBottom
import androidx.fragment.app.FragmentActivity
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.transform.BlurTransformation
import coil.transform.RoundedCornersTransformation
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.renderscript.Toolkit
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.databinding.*
import me.cniekirk.flex.util.*
import timber.log.Timber

enum class ViewType {
    IMAGE,
    VIDEO,
    SELF_TEXT,
    GALLERY,
    LINK
}

class SubmissionListAdapter(
    private val submissionsActionListener: SubmissionActionListener)
    : PagingDataAdapter<AuthedSubmission, RecyclerView.ViewHolder>(SubmissionComparator) {

    private var player: SimpleExoPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.IMAGE.ordinal -> {
                ImageSubmissionViewHolder(
                    ImageListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
            ViewType.VIDEO.ordinal -> {
                VideoSubmissionViewHolder(
                    VideoListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
            ViewType.GALLERY.ordinal -> {
                GallerySubmissionViewHolder(
                    GalleryListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
            ViewType.SELF_TEXT.ordinal -> {
                SelfTextSubmissionViewHolder(
                    SelfTextListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
            ViewType.LINK.ordinal -> {
                ExternalLinkSubmissionViewHolder(
                    LinkListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
            else -> {
                SelfTextSubmissionViewHolder(
                    SelfTextListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        val item = getItem(position)
        if (item?.isSelf == true) {
            return ViewType.SELF_TEXT.ordinal
        }
        item?.urlOverriddenByDest?.processLink {
            viewType = when (it) {
                Link.ExternalLink -> { ViewType.LINK.ordinal }
                is Link.ImageLink -> { ViewType.IMAGE.ordinal }
                Link.RedGifLink -> { ViewType.IMAGE.ordinal }
                Link.RedditGallery -> { ViewType.GALLERY.ordinal }
                Link.RedditVideo -> { ViewType.VIDEO.ordinal }
                is Link.TwitterLink -> { ViewType.LINK.ordinal }
                is Link.VideoLink -> { ViewType.VIDEO.ordinal }
            }
        }
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { post ->
            if (post.isSelf == true) {
                val selfTextHolder = (holder as SelfTextSubmissionViewHolder)
                selfTextHolder.bind(post)
            } else {
                getItem(position)?.urlOverriddenByDest?.processLink {
                    when (it) {
                        Link.ExternalLink -> {
                            val linkHolder = (holder as ExternalLinkSubmissionViewHolder)
                            linkHolder.bind(post)
                        }
                        is Link.ImageLink -> {
                            val imageHolder = (holder as ImageSubmissionViewHolder)
                            imageHolder.bind(post, it.url)
                        }
                        Link.RedGifLink -> {
                            val imageHolder = (holder as ImageSubmissionViewHolder)
                            imageHolder.bind(post, post.media?.oembed?.thumbnailUrl ?: "")
                        }
                        Link.RedditGallery -> {
                            val galleryHolder = (holder as GallerySubmissionViewHolder)
                            galleryHolder.bind(post)
                        }
                        Link.RedditVideo -> {
                            val url = if (post.crosspostParentList.isNullOrEmpty()) {
                                post.media?.redditVideo?.dashUrl ?: post.media?.redditVideo?.fallbackUrl!!
                            } else {
                                post.crosspostParentList[0].media?.redditVideo?.dashUrl
                                    ?: post.crosspostParentList[0].media?.redditVideo?.fallbackUrl!!
                            }
                            val videoHolder = (holder as VideoSubmissionViewHolder)
                            videoHolder.bind(post, url)
                        }
                        is Link.TwitterLink -> {
                            val linkHolder = (holder as ExternalLinkSubmissionViewHolder)
                            linkHolder.bind(post)
                        }
                        is Link.VideoLink -> {
                            val videoHolder = (holder as VideoSubmissionViewHolder)
                            videoHolder.bind(post, it.url)
                        }
                    }
                }
            }
        }
    }

    inner class SelfTextSubmissionViewHolder(
        private val binding: SelfTextListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission) {
            binding.root.setOnClickListener { submissionsActionListener.onPostClicked(post) }
            binding.textSubmissionTitle.text = post.title
            binding.textSubredditName.text = post.subreddit
            binding.textCommentsCount.text = post.numComments?.condense()
            binding.textUpvoteCount.text = post.ups?.condense()
            binding.textTimeSincePost.text = post.created?.toLong()?.getElapsedTime()
            binding.textSubmissionAuthor.text = binding.root.context.getString(R.string.author_format, post.author)
            post.linkFlairText?.let {
                binding.textSubmissionFlair.visibility = View.VISIBLE
                binding.textSubmissionFlair.text = it
                if (post.linkFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.linkFlairBackgroundColor))
                }
                if (post.linkFlairTextColor.equals("dark", true)) {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.white))
                }
            } ?: run {
                binding.textSubmissionFlair.visibility = View.GONE
            }
            if (post.authorFlairText.isNullOrBlank()) {
                binding.textAuthorFlair.visibility = View.GONE
            } else {
                binding.textAuthorFlair.visibility = View.VISIBLE
                binding.textAuthorFlair.text = post.authorFlairText
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
            }
            if (post.stickied!!) {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.green))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.BOLD)
                binding.submissionPin.visibility = View.VISIBLE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.submissionPin.id,
                    ConstraintSet.END,
                    binding.root.context.resources.getDimension(R.dimen.spacing_s).toInt())
                cs.applyTo(binding.root)
            } else {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.black))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.NORMAL)
                binding.submissionPin.visibility = View.GONE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.textSubredditName.id,
                    ConstraintSet.START)
                cs.applyTo(binding.root)
            }
            binding.selftextPreview.textSubmissionContentPreview.text = post.selftext?.selfTextPreview()
        }
    }

    inner class ImageSubmissionViewHolder(
        private val binding: ImageListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission, imageUrl: String) {
            binding.root.setOnClickListener { submissionsActionListener.onPostClicked(post) }
            binding.textSubmissionTitle.text = post.title
            binding.textSubredditName.text = post.subreddit
            binding.textCommentsCount.text = post.numComments?.condense()
            binding.textUpvoteCount.text = post.ups?.condense()
            binding.textTimeSincePost.text = post.created?.toLong()?.getElapsedTime()
            binding.textSubmissionAuthor.text = binding.root.context.getString(R.string.author_format, post.author)
            post.linkFlairText?.let {
                binding.textSubmissionFlair.visibility = View.VISIBLE
                binding.textSubmissionFlair.text = it
                if (post.linkFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.linkFlairBackgroundColor))
                }
                if (post.linkFlairTextColor.equals("dark", true)) {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.white))
                }
            } ?: run {
                binding.textSubmissionFlair.visibility = View.GONE
            }
            if (post.authorFlairText.isNullOrBlank()) {
                binding.textAuthorFlair.visibility = View.GONE
            } else {
                binding.textAuthorFlair.visibility = View.VISIBLE
                binding.textAuthorFlair.text = post.authorFlairText
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
            }
            if (post.stickied!!) {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.green))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.BOLD)
                binding.submissionPin.visibility = View.VISIBLE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.submissionPin.id,
                    ConstraintSet.END,
                    binding.root.context.resources.getDimension(R.dimen.spacing_s).toInt())
                cs.applyTo(binding.root)
            } else {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.black))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.NORMAL)
                binding.submissionPin.visibility = View.GONE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.textSubredditName.id,
                    ConstraintSet.START)
                cs.applyTo(binding.root)
            }
            binding.imagePreview.submissionImage.load(imageUrl) {
                crossfade(true)
                if (post.over18) {
                    transformations(BlurTransformation(
                        binding.root.context,
                        25f, 8f
                    ))
                }
            }
        }
    }

    inner class VideoSubmissionViewHolder(
        private val binding: VideoListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission, videoUrl: String) {
            binding.root.setOnClickListener { submissionsActionListener.onPostClicked(post) }
            binding.textSubmissionTitle.text = post.title
            binding.textSubredditName.text = post.subreddit
            binding.textCommentsCount.text = post.numComments?.condense()
            binding.textUpvoteCount.text = post.ups?.condense()
            binding.textTimeSincePost.text = post.created?.toLong()?.getElapsedTime()
            binding.textSubmissionAuthor.text = binding.root.context.getString(R.string.author_format, post.author)
            post.linkFlairText?.let {
                binding.textSubmissionFlair.visibility = View.VISIBLE
                binding.textSubmissionFlair.text = it
                if (post.linkFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.linkFlairBackgroundColor))
                }
                if (post.linkFlairTextColor.equals("dark", true)) {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.white))
                }
            } ?: run {
                binding.textSubmissionFlair.visibility = View.GONE
            }
            if (post.authorFlairText.isNullOrBlank()) {
                binding.textAuthorFlair.visibility = View.GONE
            } else {
                binding.textAuthorFlair.visibility = View.VISIBLE
                binding.textAuthorFlair.text = post.authorFlairText
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
            }
            if (post.stickied!!) {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.green))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.BOLD)
                binding.submissionPin.visibility = View.VISIBLE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.submissionPin.id,
                    ConstraintSet.END,
                    binding.root.context.resources.getDimension(R.dimen.spacing_s).toInt())
                cs.applyTo(binding.root)
            } else {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.black))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.NORMAL)
                binding.submissionPin.visibility = View.GONE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.textSubredditName.id,
                    ConstraintSet.START)
                cs.applyTo(binding.root)
            }
            player = binding.videoPreview.videoPlayer.initialise(videoUrl)
        }
    }

    inner class GallerySubmissionViewHolder(
        private val binding: GalleryListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission) {
            binding.root.setOnClickListener { submissionsActionListener.onPostClicked(post) }
            binding.textSubmissionTitle.text = post.title
            binding.textSubredditName.text = post.subreddit
            binding.textCommentsCount.text = post.numComments?.condense()
            binding.textUpvoteCount.text = post.ups?.condense()
            binding.textTimeSincePost.text = post.created?.toLong()?.getElapsedTime()
            binding.textSubmissionAuthor.text = binding.root.context.getString(R.string.author_format, post.author)
            post.linkFlairText?.let {
                binding.textSubmissionFlair.visibility = View.VISIBLE
                binding.textSubmissionFlair.text = it
                if (post.linkFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.linkFlairBackgroundColor))
                }
                if (post.linkFlairTextColor.equals("dark", true)) {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.white))
                }
            } ?: run {
                binding.textSubmissionFlair.visibility = View.GONE
            }
            if (post.authorFlairText.isNullOrBlank()) {
                binding.textAuthorFlair.visibility = View.GONE
            } else {
                binding.textAuthorFlair.visibility = View.VISIBLE
                binding.textAuthorFlair.text = post.authorFlairText
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
            }
            if (post.stickied!!) {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.green))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.BOLD)
                binding.submissionPin.visibility = View.VISIBLE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.submissionPin.id,
                    ConstraintSet.END,
                    binding.root.context.resources.getDimension(R.dimen.spacing_s).toInt())
                cs.applyTo(binding.root)
            } else {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.black))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.NORMAL)
                binding.submissionPin.visibility = View.GONE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.textSubredditName.id,
                    ConstraintSet.START)
                cs.applyTo(binding.root)
            }

            val media = post.mediaMetadata?.values?.toList()
            binding.mediaGalleryPreview.textGalleryCount.text =
                binding.root.context.getString(R.string.image_gallery_submission_label, media?.size)
            if (media?.size!! > 2) {
                binding.mediaGalleryPreview.secondImage.visibility = View.VISIBLE
                binding.mediaGalleryPreview.firstImage.load(
                    binding.root.context.getString(R.string.reddit_image_url,
                        media[0].id,
                        media[0].m.substring(media[0].m.indexOf('/') + 1))) {
                    crossfade(true)
                    if (post.over18) {
                        transformations(BlurTransformation(
                            binding.root.context,
                            25f, 8f
                        ))
                    }
                }
                binding.mediaGalleryPreview.secondImage.load(
                    binding.root.context.getString(R.string.reddit_image_url,
                        media[1].id,
                        media[1].m.substring(media[1].m.indexOf('/') + 1))) {
                    crossfade(true)
                    if (post.over18) {
                        transformations(BlurTransformation(
                            binding.root.context,
                            25f, 8f
                        ))
                    }
                }
                binding.mediaGalleryPreview.thirdImage.load(
                    binding.root.context.getString(R.string.reddit_image_url,
                        media[2].id,
                        media[2].m.substring(media[2].m.indexOf('/') + 1))) {
                    crossfade(true)
                    if (post.over18) {
                        transformations(BlurTransformation(
                            binding.root.context,
                            25f, 8f
                        ))
                    }
                }
            } else {
                binding.mediaGalleryPreview.secondImage.visibility = View.GONE
                binding.mediaGalleryPreview.firstImage.load(
                    binding.root.context.getString(R.string.reddit_image_url,
                        media[0].id,
                        media[0].m.substring(media[0].m.indexOf('/') + 1))) {
                    crossfade(true)
                    if (post.over18) {
                        transformations(BlurTransformation(
                            binding.root.context,
                            25f, 8f
                        ))
                    }
                }
                binding.mediaGalleryPreview.thirdImage.load(
                    binding.root.context.getString(R.string.reddit_image_url,
                        media[1].id,
                        media[1].m.substring(media[1].m.indexOf('/') + 1))) {
                    crossfade(true)
                    if (post.over18) {
                        transformations(BlurTransformation(
                            binding.root.context,
                            25f, 8f
                        ))
                    }
                }
            }
            binding.mediaGalleryPreview.root.setOnClickListener {
                submissionsActionListener.onGalleryClicked(post)
            }
        }
    }

    inner class ExternalLinkSubmissionViewHolder(
        private val binding: LinkListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission) {
            binding.root.setOnClickListener { submissionsActionListener.onPostClicked(post) }
            binding.textSubmissionTitle.text = post.title
            binding.textSubredditName.text = post.subreddit
            binding.textCommentsCount.text = post.numComments?.condense()
            binding.textUpvoteCount.text = post.ups?.condense()
            binding.textTimeSincePost.text = post.created?.toLong()?.getElapsedTime()
            binding.textSubmissionAuthor.text = binding.root.context.getString(R.string.author_format, post.author)
            post.linkFlairText?.let {
                binding.textSubmissionFlair.visibility = View.VISIBLE
                binding.textSubmissionFlair.text = it
                if (post.linkFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.linkFlairBackgroundColor))
                }
                if (post.linkFlairTextColor.equals("dark", true)) {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textSubmissionFlair.setTextColor(binding.root.resources.getColor(R.color.white))
                }
            } ?: run {
                binding.textSubmissionFlair.visibility = View.GONE
            }
            if (post.authorFlairText.isNullOrBlank()) {
                binding.textAuthorFlair.visibility = View.GONE
            } else {
                binding.textAuthorFlair.visibility = View.VISIBLE
                binding.textAuthorFlair.text = post.authorFlairText
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
            }
            if (post.stickied!!) {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.green))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.BOLD)
                binding.submissionPin.visibility = View.VISIBLE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.submissionPin.id,
                    ConstraintSet.END,
                    binding.root.context.resources.getDimension(R.dimen.spacing_s).toInt())
                cs.applyTo(binding.root)
            } else {
                binding.textSubmissionAuthor.setTextColor(binding.root.context.getColor(R.color.black))
                binding.textSubmissionAuthor.setTypeface(binding.textSubmissionAuthor.typeface, Typeface.NORMAL)
                binding.submissionPin.visibility = View.GONE
                val cs = ConstraintSet()
                cs.clone(binding.root)
                cs.connect(
                    binding.textSubmissionAuthor.id,
                    ConstraintSet.START,
                    binding.textSubredditName.id,
                    ConstraintSet.START)
                cs.applyTo(binding.root)
            }
            binding.externalLinkPreview.linkContent.text = post.url
            binding.externalLinkPreview.linkImage.load(post.preview?.images?.lastOrNull()?.resolutions?.lastOrNull()?.url) {
                crossfade(true)
                transformations(RoundedCornersTransformation(topLeft = binding.root.resources.getDimension(R.dimen.spacing_m), topRight = binding.root.resources.getDimension(R.dimen.spacing_m)))
            }
        }
    }

    fun dispose() {
        player?.release()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (player?.isPlaying == false) {
            player?.play()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        player?.pause()
        player?.seekTo(0)
        super.onViewRecycled(holder)
    }

    object SubmissionComparator : DiffUtil.ItemCallback<AuthedSubmission>() {
        override fun areItemsTheSame(oldItem: AuthedSubmission, newItem: AuthedSubmission) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AuthedSubmission, newItem: AuthedSubmission) = oldItem == newItem
    }

    interface SubmissionActionListener {
        fun onPostClicked(post: AuthedSubmission)
        fun onGalleryClicked(post: AuthedSubmission)
    }
}