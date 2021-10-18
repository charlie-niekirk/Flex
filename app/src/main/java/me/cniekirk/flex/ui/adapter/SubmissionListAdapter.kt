package me.cniekirk.flex.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.exoplayer2.SimpleExoPlayer
import im.ene.toro.ToroPlayer
import im.ene.toro.ToroUtil
import im.ene.toro.exoplayer.ExoCreator
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import im.ene.toro.exoplayer.Playable
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.data.remote.model.Resolution
import me.cniekirk.flex.databinding.*
import me.cniekirk.flex.ui.model.UserPreferences
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
    private val submissionsActionListener: SubmissionActionListener,
    private val userPreferences: UserPreferences,
    private val exoCreator: ExoCreator)
    : PagingDataAdapter<AuthedSubmission, RecyclerView.ViewHolder>(SubmissionComparator) {

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
                    ), exoCreator)
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
                Link.StreamableLink -> { ViewType.VIDEO.ordinal }
                Link.GfycatLink -> { ViewType.VIDEO.ordinal }
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
                            Timber.d("External link")
                            val linkHolder = (holder as ExternalLinkSubmissionViewHolder)
                            linkHolder.bind(post)
                        }
                        is Link.ImageLink -> {
                            Timber.d("Image link")
                            val imageHolder = (holder as ImageSubmissionViewHolder)
                            imageHolder.bind(post, it.url)
                        }
                        Link.RedGifLink -> {
                            Timber.d("Redgif link")
                            val imageHolder = (holder as ImageSubmissionViewHolder)
                            imageHolder.bind(post, post.media?.oembed?.thumbnailUrl ?: "")
                        }
                        Link.RedditGallery -> {
                            Timber.d("Gallery link")
                            val galleryHolder = (holder as GallerySubmissionViewHolder)
                            galleryHolder.bind(post)
                        }
                        Link.RedditVideo -> {
                            Timber.d("Reddit V link")
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
                            Timber.d("Twitter link")
                            val linkHolder = (holder as ExternalLinkSubmissionViewHolder)
                            linkHolder.bind(post)
                        }
                        is Link.VideoLink -> {
                            Timber.d("Video link")
                            val videoHolder = (holder as VideoSubmissionViewHolder)
                            videoHolder.bind(post, it.url)
                        }
                        is Link.StreamableLink -> {
                            Timber.d("Streamable link")
                            val viewHolder = (holder as VideoSubmissionViewHolder)
                            viewHolder.bind(post, post.url)
                        }
                        Link.GfycatLink -> {
                            Timber.d("Gfycat link: ${post.url}")
                            val viewHolder = (holder as VideoSubmissionViewHolder)
                            viewHolder.bind(post, post.url)
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
            binding.root.setOnLongClickListener {
                submissionsActionListener.onPostLongClicked(post)
                true
            }
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
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
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
            val topAwards = post.allAwardings?.sortedByDescending { it.count }?.take(3)
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
            binding.selftextPreview.textSubmissionContentPreview.text = post.selftext?.selfTextPreview()
        }
    }

    inner class ImageSubmissionViewHolder(
        private val binding: ImageListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission, imageUrl: String) {
            binding.root.setOnClickListener { submissionsActionListener.onPostClicked(post) }
            binding.root.setOnLongClickListener {
                submissionsActionListener.onPostLongClicked(post)
                true
            }
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
            val topAwards = post.allAwardings?.sortedByDescending { it.count }?.take(3)
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
            post.preview?.let {
                val resolution = binding.imagePreview.submissionImage.getSuitablePreview(post.preview.images[0].resolutions)
                resolution?.let {
                    Glide.with(binding.imagePreview.submissionImage)
                        .load(resolution.url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.imagePreview.submissionImage)
                }
            } ?: run {
                Glide.with(binding.imagePreview.submissionImage)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.imagePreview.submissionImage)
            }

        }
    }

    inner class VideoSubmissionViewHolder(
        private val binding: VideoListItemBinding,
        private val exoCreator: ExoCreator): RecyclerView.ViewHolder(binding.root), ToroPlayer {

        private var mediaUri: Uri? = null
        private var exoPlayerViewHelper: ExoPlayerViewHelper? = null

        fun bind(post: AuthedSubmission, videoUrl: String) {
            mediaUri = Uri.parse(videoUrl)

            // Load the thumbnail
            post.preview?.let {
                val resolution = binding.videoThumbnail.getSuitablePreview(post.preview.images[0].resolutions)
                resolution?.let {
                    binding.videoThumbnail.visibility = View.VISIBLE
                    Glide.with(binding.root.context)
                        .load(resolution.url)
                        .into(binding.videoThumbnail)
                }
            }

            binding.root.setOnClickListener { submissionsActionListener.onPostClicked(post) }
            binding.root.setOnLongClickListener {
                submissionsActionListener.onPostLongClicked(post)
                true
            }
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
            val topAwards = post.allAwardings?.sortedByDescending { it.count }?.take(3)
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
        }

        override fun getPlayerView() = binding.videoPlayer

        override fun getCurrentPlaybackInfo(): PlaybackInfo {
            return if (exoPlayerViewHelper != null && mediaUri != null) {
                (exoPlayerViewHelper as ExoPlayerViewHelper).latestPlaybackInfo
            } else {
                PlaybackInfo()
            }
        }

        override fun initialize(container: Container, playbackInfo: PlaybackInfo) {
            mediaUri?.let {
                if (exoPlayerViewHelper == null) {
                    exoPlayerViewHelper = ExoPlayerViewHelper(this, mediaUri as Uri, null, exoCreator)
                    exoPlayerViewHelper?.addEventListener(object : Playable.DefaultEventListener() {
                        override fun onRenderedFirstFrame() {
                            Glide.with(binding.root).clear(binding.videoThumbnail)
                            binding.videoThumbnail.visibility = View.GONE
                        }
                    })
                }
                exoPlayerViewHelper?.initialize(container, playbackInfo)
            }
        }

        override fun play() {
            exoPlayerViewHelper?.let { helper ->
                mediaUri?.let {
                    helper.play()
                }
            }
        }

        override fun pause() {
            exoPlayerViewHelper?.pause()
        }

        override fun isPlaying() = exoPlayerViewHelper != null &&
                (exoPlayerViewHelper as ExoPlayerViewHelper).isPlaying

        override fun release() {
            exoPlayerViewHelper?.let { helper ->
                helper.release()
                exoPlayerViewHelper = null
            }
        }

        override fun wantsToPlay() =
            mediaUri != null && ToroUtil.visibleAreaOffset(this, binding.root.parent) >= 0.85

        override fun getPlayerOrder() = bindingAdapterPosition
    }

    inner class GallerySubmissionViewHolder(
        private val binding: GalleryListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission) {
            binding.root.setOnClickListener { submissionsActionListener.onPostClicked(post) }
            binding.root.setOnLongClickListener {
                submissionsActionListener.onPostLongClicked(post)
                true
            }
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

            val topAwards = post.allAwardings?.sortedByDescending { it.count }?.take(3)
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

            val media = post.mediaMetadata?.values?.toList()
            binding.mediaGalleryPreview.textGalleryCount.text =
                binding.root.context.getString(R.string.image_gallery_submission_label, media?.size)
            media?.let {
                if (media.size > 2) {
                    binding.mediaGalleryPreview.secondImage.visibility = View.VISIBLE
                    Glide.with(binding.mediaGalleryPreview.firstImage)
                        .load(binding.root.context.getString(R.string.reddit_image_url,
                            media[0].id,
                            media[0].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.mediaGalleryPreview.firstImage)
                    Glide.with(binding.mediaGalleryPreview.secondImage)
                        .load(binding.root.context.getString(R.string.reddit_image_url,
                            media[1].id,
                            media[1].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.mediaGalleryPreview.secondImage)
                    Glide.with(binding.mediaGalleryPreview.thirdImage)
                        .load(binding.root.context.getString(R.string.reddit_image_url,
                            media[2].id,
                            media[2].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.mediaGalleryPreview.thirdImage)
                } else {
                    binding.mediaGalleryPreview.secondImage.visibility = View.GONE
                    Glide.with(binding.mediaGalleryPreview.firstImage)
                        .load(binding.root.context.getString(R.string.reddit_image_url,
                            media[0].id,
                            media[0].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.mediaGalleryPreview.firstImage)
                    Glide.with(binding.mediaGalleryPreview.thirdImage)
                        .load(binding.root.context.getString(R.string.reddit_image_url,
                            media[1].id,
                            media[1].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }))                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.mediaGalleryPreview.thirdImage)
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
            binding.root.setOnLongClickListener {
                submissionsActionListener.onPostLongClicked(post)
                true
            }
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
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
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
            val topAwards = post.allAwardings?.sortedByDescending { it.count }?.take(3)
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
            binding.externalLinkPreview.linkContent.text = post.url
            Glide.with(binding.externalLinkPreview.linkImage)
                .load(post.preview?.images?.lastOrNull()?.resolutions?.lastOrNull()?.url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(RoundedCorners(binding.root.resources.getDimension(R.dimen.spacing_m).toInt()))
                .into(binding.externalLinkPreview.linkImage)
        }
    }

    private fun ImageView.getSuitablePreview(previews: List<Resolution>): Resolution? {
        if (previews.isNotEmpty()) {
            var preview = previews.last()
            if (preview.width * preview.height > 1000000) {
                for (i in previews.size - 1 downTo 0) {
                    preview = previews[i]
                    if (width >= preview.width) {
                        if (preview.width * preview.height <= 1000000) {
                            return preview
                        }
                    } else {
                        val height = width / preview.width * preview.height
                        if (width * height <= 1000000) {
                            return preview
                        }
                    }
                }
            }
            return preview
        }
        return null
    }

    object SubmissionComparator : DiffUtil.ItemCallback<AuthedSubmission>() {
        override fun areItemsTheSame(oldItem: AuthedSubmission, newItem: AuthedSubmission) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AuthedSubmission, newItem: AuthedSubmission) = oldItem == newItem
    }

    interface SubmissionActionListener {
        fun onPostClicked(post: AuthedSubmission)
        fun onPostLongClicked(post: AuthedSubmission)
        fun onGalleryClicked(post: AuthedSubmission)
    }
}