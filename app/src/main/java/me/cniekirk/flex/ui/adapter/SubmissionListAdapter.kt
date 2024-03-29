package me.cniekirk.flex.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import im.ene.toro.ToroPlayer
import im.ene.toro.ToroUtil
import im.ene.toro.exoplayer.ExoCreator
import im.ene.toro.exoplayer.ExoPlayerViewHelper
import im.ene.toro.exoplayer.Playable
import im.ene.toro.media.PlaybackInfo
import im.ene.toro.widget.Container
import io.noties.markwon.SpannableBuilder
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Resolution
import me.cniekirk.flex.databinding.*
import me.cniekirk.flex.ui.text.getBionikSpanForText
import me.cniekirk.flex.ui.util.Size2
import me.cniekirk.flex.ui.view.CenteredImageSpan
import me.cniekirk.flex.util.*
import timber.log.Timber

enum class ViewType {
    IMAGE,
    VIDEO,
    YOUTUBE,
    SELF_TEXT,
    GALLERY,
    IMGUR_GALLERY,
    LINK,
    TWITTER,
    POLL
}

class SubmissionListAdapter(
    private val submissionsActionListener: SubmissionActionListener,
    private val settings: FlexSettings,
    private val exoCreator: ExoCreator
) : PagingDataAdapter<AuthedSubmission, RecyclerView.ViewHolder>(SubmissionComparator) {

    private val sizeOptions by lazy(LazyThreadSafetyMode.NONE) {
        RequestOptions()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ViewType.IMAGE.ordinal -> {
                ImageSubmissionViewHolder(
                    ImageListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
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
            ViewType.POLL.ordinal -> {
                PollViewHolder(
                    PollListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
            ViewType.YOUTUBE.ordinal -> {
                YoutubeViewHolder(
                    YoutubeListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ViewType.IMGUR_GALLERY.ordinal -> {
                GallerySubmissionViewHolder(
                    GalleryListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ))
            }
            ViewType.TWITTER.ordinal -> {
                TwitterViewHolder(
                    TweetListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
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
        if (item?.pollData != null) {
            return ViewType.POLL.ordinal
        }
        if (item?.isSelf == true) {
            return ViewType.SELF_TEXT.ordinal
        }
        item?.urlOverriddenByDest?.processLink {
            viewType = when (it) {
                Link.ExternalLink -> { ViewType.LINK.ordinal }
                is Link.ImageLink -> { ViewType.IMAGE.ordinal }
                Link.RedGifLink -> { ViewType.IMAGE.ordinal }
                Link.RedditGallery -> { ViewType.GALLERY.ordinal }
                is Link.ImgurGalleryLink -> {
                    if (item.imgurGalleryLinks?.size!! > 1) {
                        ViewType.IMGUR_GALLERY.ordinal
                    } else {
                        ViewType.IMAGE.ordinal
                    }
                }
                Link.RedditVideo -> { ViewType.VIDEO.ordinal }
                is Link.TwitterLink -> { ViewType.TWITTER.ordinal }
                is Link.VideoLink -> { ViewType.VIDEO.ordinal }
                is Link.YoutubeLink -> { ViewType.YOUTUBE.ordinal }
                Link.StreamableLink -> { ViewType.VIDEO.ordinal }
                Link.GfycatLink -> { ViewType.VIDEO.ordinal }
            }
        }
        return viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let { post ->
            if (post.pollData != null) {
                val pollHolder = (holder as PollViewHolder)
                pollHolder.bind(post)
            } else if (post.isSelf == true) {
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
                            imageHolder.bind(post, post.url)
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
                            val linkHolder = (holder as TwitterViewHolder)
                            linkHolder.bind(post)
                        }
                        is Link.VideoLink -> {
                            val videoHolder = (holder as VideoSubmissionViewHolder)
                            videoHolder.bind(post, it.url)
                        }
                        is Link.StreamableLink -> {
                            val viewHolder = (holder as VideoSubmissionViewHolder)
                            viewHolder.bind(post, post.url)
                        }
                        Link.GfycatLink -> {
                            val viewHolder = (holder as VideoSubmissionViewHolder)
                            viewHolder.bind(post, post.url)
                        }
                        is Link.ImgurGalleryLink -> {
                            if (post.imgurGalleryLinks?.size!! > 1) {
                                val galleryHolder = (holder as GallerySubmissionViewHolder)
                                galleryHolder.bind(post)
                            } else {
                                val imageHolder = (holder as ImageSubmissionViewHolder)
                                imageHolder.bind(post, post.imgurGalleryLinks!!.first())
                            }
                        }
                        is Link.YoutubeLink -> {
                            val viewHolder = (holder as YoutubeViewHolder)
                            viewHolder.bind(post, it.videoId)
                        }
                    }
                }
            }
        }
    }

    inner class SelfTextSubmissionViewHolder(
        private val binding: SelfTextListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission) {
            binding.actions.root.visibility = View.GONE
            binding.root.setOnClickListener {
                submissionsActionListener.onPostClicked(post)
            }
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
                if (post.authorFlairRichtext.isNullOrEmpty()) {
                    if (post.author.equals("tommyinnit", true)) {
                        binding.textAuthorFlair.text = post.authorFlairText + " 5'10\""
                    } else {
                        binding.textAuthorFlair.text = post.authorFlairText
                    }
                } else {
                    val spannable = SpannableString(post.authorFlairText)
                    post.authorFlairRichtext.forEach {
                        if (it.e.equals("emoji", true)) {
                            Glide.with(binding.root.context)
                                .asDrawable()
                                .load(it.u)
                                .into(object : CustomTarget<Drawable>(){
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                        resource.setBounds(0, 0, binding.textAuthorFlair.lineHeight, binding.textAuthorFlair.lineHeight)
                                        val image = CenteredImageSpan(resource)
                                        spannable.setSpan(image, spannable.indexOf(it.a!!), spannable.indexOf(it.a) + it.a.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                        binding.textAuthorFlair.text = spannable
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
                    }
                    binding.textAuthorFlair.text = spannable
                }
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
                if (post.authorFlairTextColor.equals("dark", true)) {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.white))
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
                binding.textSubmissionAuthor.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
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
            if (post.selftext?.isEmpty() == true) {
                binding.selftextPreview.textSubmissionContentPreview.visibility = View.GONE
            } else {
                binding.selftextPreview.textSubmissionContentPreview.visibility = View.VISIBLE
                binding.selftextPreview.textSubmissionContentPreview.text = post.selftext?.selfTextPreview()
            }
        }
    }

    inner class ImageSubmissionViewHolder(
        private val binding: ImageListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission, imageUrl: String) {
            binding.actions.root.visibility = View.GONE
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
                if (post.authorFlairRichtext.isNullOrEmpty()) {
                    if (post.author.equals("tommyinnit", true)) {
                        binding.textAuthorFlair.text = post.authorFlairText + " 5'10\""
                    } else {
                        binding.textAuthorFlair.text = post.authorFlairText
                    }
                } else {
                    val spannable = SpannableString(post.authorFlairText)
                    post.authorFlairRichtext.forEach {
                        if (it.e.equals("emoji", true)) {
                            Glide.with(binding.root.context)
                                .asDrawable()
                                .load(it.u)
                                .into(object : CustomTarget<Drawable>(){
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                        resource.setBounds(0, 0, binding.textAuthorFlair.lineHeight, binding.textAuthorFlair.lineHeight)
                                        val image = CenteredImageSpan(resource)
                                        spannable.setSpan(image, spannable.indexOf(it.a!!), spannable.indexOf(it.a) + it.a.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                        binding.textAuthorFlair.text = spannable
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
                    }
                    binding.textAuthorFlair.text = spannable
                }
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
                if (post.authorFlairTextColor.equals("dark", true)) {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.white))
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
                binding.textSubmissionAuthor.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
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
            val hideNsfw = settings.profilesList.first().blurNsfw
            if (post.preview != null) {
                val resolution = binding.imagePreview.submissionImage.getSuitablePreview(post.preview.images[0].resolutions)
                resolution?.let {
                    Glide.with(binding.imagePreview.submissionImage)
                        .`as`(Size2::class.java)
                        .apply(sizeOptions)
                        .load(resolution.url)
                        .into(object : SimpleTarget<Size2>() {
                            override fun onResourceReady(size: Size2, glideAnimation: Transition<in Size2>?) {
                                binding.imagePreview.submissionImage.ratio = size.height.toFloat() / size.width.toFloat()
                                binding.imagePreview.submissionImage.loadImage(resolution.url, hideNsfw && post.over18)
                            }
                            override fun onLoadFailed(errorDrawable: Drawable?) {}
                        })
                }
            } else {
                binding.imagePreview.submissionImage.loadImage(imageUrl, hideNsfw && post.over18)
            }
        }
    }

    inner class VideoSubmissionViewHolder(
        private val binding: VideoListItemBinding,
        private val exoCreator: ExoCreator): RecyclerView.ViewHolder(binding.root), ToroPlayer {

        private var mediaUri: Uri? = null
        private var exoPlayerViewHelper: ExoPlayerViewHelper? = null

        fun bind(post: AuthedSubmission, videoUrl: String) {
            binding.actions.root.visibility = View.GONE

            mediaUri = Uri.parse(videoUrl)

            val hideNsfw = settings.profilesList.first().blurNsfw
            // Load the thumbnail
            post.preview?.let {
                val resolution = binding.videoThumbnail.getSuitablePreview(post.preview.images[0].resolutions)
                resolution?.let {
                    binding.videoThumbnail.visibility = View.VISIBLE
                    binding.videoThumbnail.loadImage(resolution.url, hideNsfw && post.over18)
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
                if (post.authorFlairRichtext.isNullOrEmpty()) {
                    if (post.author.equals("tommyinnit", true)) {
                        binding.textAuthorFlair.text = post.authorFlairText + " 5'10\""
                    } else {
                        binding.textAuthorFlair.text = post.authorFlairText
                    }
                } else {
                    val spannable = SpannableString(post.authorFlairText)
                    post.authorFlairRichtext.forEach {
                        if (it.e.equals("emoji", true)) {
                            Glide.with(binding.root.context)
                                .asDrawable()
                                .load(it.u)
                                .into(object : CustomTarget<Drawable>(){
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                        resource.setBounds(0, 0, binding.textAuthorFlair.lineHeight, binding.textAuthorFlair.lineHeight)
                                        val image = CenteredImageSpan(resource)
                                        spannable.setSpan(image, spannable.indexOf(it.a!!), spannable.indexOf(it.a) + it.a.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                        binding.textAuthorFlair.text = spannable
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
                    }
                    binding.textAuthorFlair.text = spannable
                }
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
                if (post.authorFlairTextColor.equals("dark", true)) {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.white))
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
                binding.textSubmissionAuthor.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
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
            binding.actions.root.visibility = View.GONE

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
                if (post.authorFlairRichtext.isNullOrEmpty()) {
                    if (post.author.equals("tommyinnit", true)) {
                        binding.textAuthorFlair.text = post.authorFlairText + " 5'10\""
                    } else {
                        binding.textAuthorFlair.text = post.authorFlairText
                    }
                } else {
                    val spannable = SpannableString(post.authorFlairText)
                    post.authorFlairRichtext.forEach {
                        if (it.e.equals("emoji", true)) {
                            Glide.with(binding.root.context)
                                .asDrawable()
                                .load(it.u)
                                .into(object : CustomTarget<Drawable>(){
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                        resource.setBounds(0, 0, binding.textAuthorFlair.lineHeight, binding.textAuthorFlair.lineHeight)
                                        val image = CenteredImageSpan(resource)
                                        spannable.setSpan(image, spannable.indexOf(it.a!!), spannable.indexOf(it.a) + it.a.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                        binding.textAuthorFlair.text = spannable
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
                    }
                    binding.textAuthorFlair.text = spannable
                }
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
                if (post.authorFlairTextColor.equals("dark", true)) {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.white))
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
                binding.textSubmissionAuthor.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
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

            post.imgurGalleryLinks?.let {
                binding.mediaGalleryPreview.textGalleryCount.text =
                    binding.root.context.getString(R.string.image_gallery_submission_label, it.size)
                if (it.size > 2) {
                    binding.mediaGalleryPreview.secondImage.visibility = View.VISIBLE
                    binding.mediaGalleryPreview.firstImage.loadImage(it[0], post.over18)
                    binding.mediaGalleryPreview.firstImage.loadImage(it[1], post.over18)
                    binding.mediaGalleryPreview.firstImage.loadImage(it[2], post.over18)
                } else {
                    binding.mediaGalleryPreview.secondImage.visibility = View.GONE
                    binding.mediaGalleryPreview.firstImage.loadImage(it[0], post.over18)
                    binding.mediaGalleryPreview.firstImage.loadImage(it[1], post.over18)
                }
            } ?: run {
                val media = post.mediaMetadata?.values?.toList()
                binding.mediaGalleryPreview.textGalleryCount.text =
                    binding.root.context.getString(R.string.image_gallery_submission_label, media?.size)
                media?.let {
                    if (media.size > 2) {
                        binding.mediaGalleryPreview.secondImage.visibility = View.VISIBLE
                        binding.mediaGalleryPreview.firstImage.loadImage(binding.root.context.getString(R.string.reddit_image_url,
                            media[0].id,
                            media[0].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }), post.over18)
                        binding.mediaGalleryPreview.secondImage.loadImage(binding.root.context.getString(R.string.reddit_image_url,
                            media[1].id,
                            media[1].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }), post.over18)
                        binding.mediaGalleryPreview.thirdImage.loadImage(binding.root.context.getString(R.string.reddit_image_url,
                            media[2].id,
                            media[2].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }), post.over18)
                    } else {
                        binding.mediaGalleryPreview.secondImage.visibility = View.GONE
                        binding.mediaGalleryPreview.firstImage.loadImage(binding.root.context.getString(R.string.reddit_image_url,
                            media[0].id,
                            media[0].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }), post.over18)
                        binding.mediaGalleryPreview.thirdImage.loadImage(binding.root.context.getString(R.string.reddit_image_url,
                            media[1].id,
                            media[1].m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" }), post.over18)
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
            binding.actions.root.visibility = View.GONE

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
                if (post.authorFlairRichtext.isNullOrEmpty()) {
                    if (post.author.equals("tommyinnit", true)) {
                        binding.textAuthorFlair.text = post.authorFlairText + " 5'10\""
                    } else {
                        binding.textAuthorFlair.text = post.authorFlairText
                    }
                } else {
                    val spannable = SpannableString(post.authorFlairText)
                    post.authorFlairRichtext.forEach {
                        if (it.e.equals("emoji", true)) {
                            Glide.with(binding.root.context)
                                .asDrawable()
                                .load(it.u)
                                .into(object : CustomTarget<Drawable>(){
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                        resource.setBounds(0, 0, binding.textAuthorFlair.lineHeight, binding.textAuthorFlair.lineHeight)
                                        val image = CenteredImageSpan(resource)
                                        spannable.setSpan(image, spannable.indexOf(it.a!!), spannable.indexOf(it.a) + it.a.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                        binding.textAuthorFlair.text = spannable
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
                    }
                    binding.textAuthorFlair.text = spannable
                }
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
                if (post.authorFlairTextColor.equals("dark", true)) {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.white))
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
                binding.textSubmissionAuthor.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
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
            Timber.d("IMAGE: ${post}")
            Glide.with(binding.externalLinkPreview.linkImage)
                .load(post.preview?.images?.lastOrNull()?.resolutions?.lastOrNull()?.url?.replace("amp;", ""))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(CenterCrop(), GranularRoundedCorners(
                    binding.root.resources.getDimension(R.dimen.spacing_m),
                    binding.root.resources.getDimension(R.dimen.spacing_m),
                    0F,
                    0F
                ))
                .into(binding.externalLinkPreview.linkImage)
        }
    }

    inner class PollViewHolder(private val binding: PollListItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission) {
            binding.actions.root.visibility = View.GONE

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
                if (post.authorFlairRichtext.isNullOrEmpty()) {
                    if (post.author.equals("tommyinnit", true)) {
                        binding.textAuthorFlair.text = post.authorFlairText + " 5'10\""
                    } else {
                        binding.textAuthorFlair.text = post.authorFlairText
                    }
                } else {
                    val spannable = SpannableString(post.authorFlairText)
                    post.authorFlairRichtext.forEach {
                        if (it.e.equals("emoji", true)) {
                            Glide.with(binding.root.context)
                                .asDrawable()
                                .load(it.u)
                                .into(object : CustomTarget<Drawable>(){
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                        resource.setBounds(0, 0, binding.textAuthorFlair.lineHeight, binding.textAuthorFlair.lineHeight)
                                        val image = CenteredImageSpan(resource)
                                        spannable.setSpan(image, spannable.indexOf(it.a!!), spannable.indexOf(it.a) + it.a.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                        binding.textAuthorFlair.text = spannable
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
                    }
                    binding.textAuthorFlair.text = spannable
                }
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
                if (post.authorFlairTextColor.equals("dark", true)) {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.white))
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
                binding.textSubmissionAuthor.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
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
            val adapter = PollOptionsAdapter()
            binding.pollPreview.pollOptions.adapter = adapter
            adapter.submitList(post.pollData!!.options)
            binding.pollPreview.voteCount.text = "${post.pollData.totalVoteCount?.condense()} votes"
            binding.pollPreview.timeRemaining.text = "${post.pollData.votingEndTimestamp!!.getElapsedTime(false)} remaining"
        }
    }

    inner class YoutubeViewHolder(private val binding: YoutubeListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission, videoId: String) {
            binding.actions.root.visibility = View.GONE

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
                if (post.authorFlairRichtext.isNullOrEmpty()) {
                    if (post.author.equals("tommyinnit", true)) {
                        binding.textAuthorFlair.text = post.authorFlairText + " 5'10\""
                    } else {
                        binding.textAuthorFlair.text = post.authorFlairText
                    }
                } else {
                    val spannable = SpannableString(post.authorFlairText)
                    post.authorFlairRichtext.forEach {
                        if (it.e.equals("emoji", true)) {
                            Glide.with(binding.root.context)
                                .asDrawable()
                                .load(it.u)
                                .into(object : CustomTarget<Drawable>(){
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                        resource.setBounds(0, 0, binding.textAuthorFlair.lineHeight, binding.textAuthorFlair.lineHeight)
                                        val image = CenteredImageSpan(resource)
                                        spannable.setSpan(image, spannable.indexOf(it.a!!), spannable.indexOf(it.a) + it.a.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                        binding.textAuthorFlair.text = spannable
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
                    }
                    binding.textAuthorFlair.text = spannable
                }
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
                if (post.authorFlairTextColor.equals("dark", true)) {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.white))
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
                binding.textSubmissionAuthor.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
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
            binding.videoPlayer.loadImage(videoId.toYtThumb(), false)
            binding.videoPlayer.setOnClickListener {
                submissionsActionListener.onYoutubeVideoClicked(videoId)
            }
        }

    }

    inner class TwitterViewHolder(private val binding: TweetListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: AuthedSubmission) {
            binding.actions.root.visibility = View.GONE

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
                if (post.authorFlairRichtext.isNullOrEmpty()) {
                    if (post.author.equals("tommyinnit", true)) {
                        binding.textAuthorFlair.text = post.authorFlairText + " 5'10\""
                    } else {
                        binding.textAuthorFlair.text = post.authorFlairText
                    }
                } else {
                    val spannable = SpannableString(post.authorFlairText)
                    post.authorFlairRichtext.forEach {
                        if (it.e.equals("emoji", true)) {
                            Glide.with(binding.root.context)
                                .asDrawable()
                                .load(it.u)
                                .into(object : CustomTarget<Drawable>(){
                                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                        resource.setBounds(0, 0, binding.textAuthorFlair.lineHeight, binding.textAuthorFlair.lineHeight)
                                        val image = CenteredImageSpan(resource)
                                        spannable.setSpan(image, spannable.indexOf(it.a!!), spannable.indexOf(it.a) + it.a.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                        binding.textAuthorFlair.text = spannable
                                    }
                                    override fun onLoadCleared(placeholder: Drawable?) {}
                                })
                        }
                    }
                }
                if (post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (post.authorFlairBackgroundColor.equals("transparent", true)) {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    binding.textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(post.authorFlairBackgroundColor))
                }
                if (post.authorFlairTextColor.equals("dark", true)) {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.black))
                } else {
                    binding.textAuthorFlair.setTextColor(binding.root.resources.getColor(R.color.white))
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
                binding.textSubmissionAuthor.setTextColor(binding.root.context.resolveColorAttr(android.R.attr.textColorPrimary))
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
            post.tweetDetails?.let { tweet ->
                tweet.includes?.media?.let { media ->
                    binding.tweetPreview.root.visibility = View.GONE
                    binding.tweetMediaPreview.root.visibility = View.VISIBLE
                    binding.tweetMediaPreview.tweetAuthorName.text = tweet.includes.users?.get(0)?.name
                    Glide.with(binding.root).load(tweet.includes.users?.get(0)?.profileImageUrl)
                        .circleCrop()
                        .into(binding.tweetMediaPreview.tweetProfileImage)
                    binding.tweetMediaPreview.tweetProfileVerified.isVisible = tweet.includes.users?.get(0)?.verified ?: false
                    binding.tweetMediaPreview.tweetBody.text = tweet.data?.text
                    if (media.first().type?.equals("photo", true) == true) {
                        Glide.with(binding.root).load(media.first().url)
                            .into(binding.tweetMediaPreview.tweetMedia)
                    }
                    if (media.first().type?.equals("video", true) == true) {
                        Glide.with(binding.root).load(media.first().previewImageUrl)
                            .into(binding.tweetMediaPreview.tweetMedia)
                    }
                } ?: run {
                    binding.tweetPreview.root.visibility = View.VISIBLE
                    binding.tweetMediaPreview.root.visibility = View.GONE
                    binding.tweetPreview.tweetAuthorName.text = tweet.includes?.users?.get(0)?.name
                    Glide.with(binding.root).load(tweet.includes?.users?.get(0)?.profileImageUrl)
                        .circleCrop()
                        .into(binding.tweetPreview.tweetProfileImage)
                    binding.tweetPreview.tweetProfileVerified.isVisible = tweet.includes?.users?.get(0)?.verified ?: false
                    binding.tweetPreview.tweetBody.text = tweet.data?.text
                }
            }
        }

    }

    private fun ImageView.getSuitablePreview(previews: List<Resolution>): Resolution? {
        if (previews.isNotEmpty()) {
            var preview = previews.last()
            if (preview.width * preview.height > 700000) {
                for (i in previews.size - 1 downTo 0) {
                    preview = previews[i]
                    if (width >= preview.width) {
                        if (preview.width * preview.height <= 700000) {
                            return preview
                        }
                    } else {
                        val height = width / preview.width * preview.height
                        if (width * height <= 700000) {
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
        fun onYoutubeVideoClicked(videoId: String)
        fun onSubredditClicked(subreddit: String)
        fun onUserClicked(username: String)
    }
}