package me.cniekirk.flex.ui.submission

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.RoundedCornersTransformation
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.transition.MaterialSharedAxis
import io.noties.markwon.Markwon
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubmissionDetailFragmentBinding
import me.cniekirk.flex.util.*
import timber.log.Timber

class SubmissionDetailFragment : Fragment(R.layout.submission_detail_fragment) {

    private var player: SimpleExoPlayer? = null

    private val args by navArgs<SubmissionDetailFragmentArgs>()
    private val markwon by lazy(LazyThreadSafetyMode.NONE) { Markwon.create(requireContext()) }
    private var binding: SubmissionDetailFragmentBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SubmissionDetailFragmentBinding.bind(view)

        binding?.apply {
            textSubmissionTitle.text = args.post.title
            textSubredditName.text = args.post.subreddit
            textUpvoteRatio.text = getString(R.string.percentage_format,
                (args.post.upvoteRatio?.times(100)?.toInt().toString()))
            textUpvoteCount.text = args.post.ups?.condense()
            textTimeSincePost.text = args.post.created?.toLong()?.getElapsedTime()
            textSubmissionAuthor.text = getString(R.string.author_format, args.post.author)
            args.post.linkFlairText?.let {
                textSubmissionFlair.visibility = View.VISIBLE
                textSubmissionFlair.text = it
                if (args.post.linkFlairBackgroundColor.isNullOrEmpty()) {
                    textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else {
                    textSubmissionFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(args.post.linkFlairBackgroundColor))
                }
                if (args.post.linkFlairTextColor.equals("dark", true)) {
                    textSubmissionFlair.setTextColor(root.resources.getColor(R.color.black))
                } else {
                    textSubmissionFlair.setTextColor(root.resources.getColor(R.color.white))
                }
            } ?: run {
                textSubmissionFlair.visibility = View.GONE
            }
            if (args.post.isSelf!!) {
                selftextPreview.textSubmissionContent.visibility = View.VISIBLE
                imagePreview.submissionImage.visibility = View.GONE
                videoPreview.videoPlayer.visibility = View.GONE
                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                markwon.setMarkdown(selftextPreview.textSubmissionContent, args.post.selftext ?: "")
            } else {
                selftextPreview.textSubmissionContent.visibility = View.GONE
                args.post.urlOverriddenByDest?.let { url ->
                    url.processLink {
                        when (it) {
                            Link.ExternalLink -> {
                                videoPreview.videoPlayer.visibility = View.GONE
                                imagePreview.submissionImage.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.VISIBLE
                                externalLinkPreview.linkContent.text = args.post.url
                                externalLinkPreview.linkImage.load(args.post.preview?.images?.lastOrNull()?.resolutions?.lastOrNull()?.url) {
                                    crossfade(true)
                                    transformations(RoundedCornersTransformation(
                                        topLeft = root.resources.getDimension(R.dimen.spacing_m),
                                        topRight = root.resources.getDimension(R.dimen.spacing_m)))
                                }
                            }
                            is Link.ImageLink -> {
                                videoPreview.videoPlayer.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                imagePreview.submissionImage.visibility = View.VISIBLE
                                imagePreview.submissionImage.load(it.url) {
                                    crossfade(true)
                                }
                            }
                            is Link.VideoLink -> {
                                imagePreview.submissionImage.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                videoPreview.videoPlayer.visibility = View.VISIBLE
                                player = videoPreview.videoPlayer.initialise(args.post.urlOverriddenByDest ?: "")
                            }
                            Link.RedditVideo -> {
                                imagePreview.submissionImage.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                videoPreview.videoPlayer.visibility = View.VISIBLE
                                Timber.d(args.post.media?.redditVideo?.dashUrl ?:
                                    args.post.media?.redditVideo?.fallbackUrl!!)
                                player = videoPreview.videoPlayer.initialise(
                                    args.post.media?.redditVideo?.dashUrl ?:
                                    args.post.media?.redditVideo?.fallbackUrl!!)
                            }
                        }
                    }
                }
            }
            if (args.post.stickied!!) {
                textSubmissionAuthor.setTextColor(root.context.getColor(R.color.green))
                textSubmissionAuthor.setTypeface(textSubmissionAuthor.typeface, Typeface.BOLD)
                submissionPin.visibility = View.VISIBLE
                val cs = ConstraintSet()
                cs.clone(root)
                cs.connect(
                    textSubmissionAuthor.id,
                    ConstraintSet.START,
                    submissionPin.id,
                    ConstraintSet.END,
                    root.context.resources.getDimension(R.dimen.spacing_s).toInt())
                cs.applyTo(root)
            } else {
                textSubmissionAuthor.setTextColor(root.context.getColor(R.color.black))
                textSubmissionAuthor.setTypeface(textSubmissionAuthor.typeface, Typeface.NORMAL)
                submissionPin.visibility = View.GONE
                val cs = ConstraintSet()
                cs.clone(root)
                cs.connect(
                    textSubmissionAuthor.id,
                    ConstraintSet.START,
                    textSubredditName.id,
                    ConstraintSet.START)
                cs.applyTo(root)
            }
        }
    }

    override fun onDestroyView() {
        player?.release()
        binding = null
        super.onDestroyView()
    }
}