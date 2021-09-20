package me.cniekirk.flex.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.exoplayer2.SimpleExoPlayer
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.AuthedSubmission
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.databinding.SubmissionListItemBinding
import me.cniekirk.flex.util.*
import timber.log.Timber

class SubmissionListAdapter(
    private val submissionsActionListener: SubmissionActionListener)
    : PagingDataAdapter<AuthedSubmission, SubmissionListAdapter.SubmissionListViewHolder>(SubmissionComparator) {

    private var player: SimpleExoPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubmissionListViewHolder {
        return SubmissionListViewHolder(
            SubmissionListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ))
    }

    override fun onBindViewHolder(holder: SubmissionListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class SubmissionListViewHolder(
        private val binding: SubmissionListItemBinding) : RecyclerView.ViewHolder(binding.root) {
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
            if (post.isSelf!!) {
                binding.selftextPreview.textSubmissionContentPreview.visibility = View.VISIBLE
                binding.imagePreview.submissionImage.visibility = View.GONE
                binding.videoPreview.videoPlayer.visibility = View.GONE
                binding.externalLinkPreview.externalLinkContainer.visibility = View.GONE
                binding.selftextPreview.textSubmissionContentPreview.text = post.selftext?.selfTextPreview()
            } else {
                binding.selftextPreview.textSubmissionContentPreview.visibility = View.GONE
                post.urlOverriddenByDest?.let { url ->
                    url.processLink {
                        when (it) {
                            Link.ExternalLink -> {
                                binding.videoPreview.videoPlayer.visibility = View.GONE
                                binding.imagePreview.submissionImage.visibility = View.GONE
                                binding.externalLinkPreview.externalLinkContainer.visibility = View.VISIBLE
                                binding.externalLinkPreview.linkContent.text = post.url
                                binding.externalLinkPreview.linkImage.load(post.preview?.images?.lastOrNull()?.resolutions?.lastOrNull()?.url) {
                                    crossfade(true)
                                    transformations(RoundedCornersTransformation(topLeft = binding.root.resources.getDimension(R.dimen.spacing_m), topRight = binding.root.resources.getDimension(R.dimen.spacing_m)))
                                }
                            }
                            is Link.ImageLink -> {
                                binding.videoPreview.videoPlayer.visibility = View.GONE
                                binding.externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                binding.imagePreview.submissionImage.visibility = View.VISIBLE
                                binding.imagePreview.submissionImage.load(it.url) {
                                    crossfade(true)
                                }
                            }
                            is Link.VideoLink -> {
                                binding.imagePreview.submissionImage.visibility = View.GONE
                                binding.externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                binding.videoPreview.videoPlayer.visibility = View.VISIBLE
                                player = binding.videoPreview.videoPlayer.initialise(post.urlOverriddenByDest)
                            }
                            Link.RedditVideo -> {
                                binding.imagePreview.submissionImage.visibility = View.GONE
                                binding.externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                binding.videoPreview.videoPlayer.visibility = View.VISIBLE
                                Timber.d(post.media?.redditVideo?.dashUrl ?: post.media?.redditVideo?.fallbackUrl!!)
                                player = binding.videoPreview.videoPlayer.initialise(
                                    post.media?.redditVideo?.dashUrl ?: post.media?.redditVideo?.fallbackUrl!!)
                            }
                            is Link.TwitterLink -> {

                            }
                        }
                    }
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

    override fun onViewRecycled(holder: SubmissionListViewHolder) {
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
    }
}