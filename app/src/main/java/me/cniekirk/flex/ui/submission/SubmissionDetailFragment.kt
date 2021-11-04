package me.cniekirk.flex.ui.submission

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.SlideDistanceProvider
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.MoreComments
import me.cniekirk.flex.databinding.SubmissionDetailFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.CommentTreeAdapter
import me.cniekirk.flex.ui.viewmodel.SubmissionDetailViewModel
import me.cniekirk.flex.util.*
import timber.log.Timber

@AndroidEntryPoint
class SubmissionDetailFragment : BaseFragment(R.layout.submission_detail_fragment), CommentTreeAdapter.CommentActionListener {

    private var player: SimpleExoPlayer? = null

    private val args by navArgs<SubmissionDetailFragmentArgs>()
    private val markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon
            .builder(requireContext())
            .usePlugin(StrikethroughPlugin())
            .usePlugin(LinkifyPlugin.create())
            .build()
    }
    private val loading by lazy(LazyThreadSafetyMode.NONE) { binding.loadingIndicator.drawable as AnimatedVectorDrawable }
    private val viewModel by viewModels<SubmissionDetailViewModel>()
    private val binding by viewBinding(SubmissionDetailFragmentBinding::bind)
    private var adapter: CommentTreeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            (primaryAnimatorProvider as SlideDistanceProvider).slideDistance =
                requireContext().resources.getDimension(R.dimen.slide_distance).toInt()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            (primaryAnimatorProvider as SlideDistanceProvider).slideDistance =
                requireContext().resources.getDimension(R.dimen.slide_distance).toInt()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottom_app_bar)
        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.hide()
            bottomAppBar.performHide()
        }
        binding.apply {
            backButton.setOnClickListener { it.findNavController().popBackStack() }

            textCommentsTitle.text = getString(R.string.comments_title, args.post.numComments?.condense())
            loading.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    loadingIndicator.post { loading.start() }
                }
            })
            loading.start()

            adapter = CommentTreeAdapter(args.post, markwon, this@SubmissionDetailFragment)
            commentsTreeList.adapter = adapter

            textSubmissionTitle.text = args.post.title
            textSubredditName.text = args.post.subreddit
            textUpvoteRatio.text = getString(R.string.percentage_format,
                (args.post.upvoteRatio.times(100).toInt().toString()))
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
                    textSubmissionFlair.setTextColor(root.context.getColor(R.color.black))
                } else {
                    textSubmissionFlair.setTextColor(root.context.getColor(R.color.white))
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
                                Glide.with(externalLinkPreview.linkImage)
                                    .load(args.post.preview?.images?.lastOrNull()?.resolutions?.lastOrNull()?.url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .transform(CenterCrop(), GranularRoundedCorners(
                                        binding.root.resources.getDimension(R.dimen.spacing_m),
                                        binding.root.resources.getDimension(R.dimen.spacing_m),
                                        0F,
                                        0F
                                    ))
                                    .into(externalLinkPreview.linkImage)
                            }
                            is Link.ImageLink -> {
                                videoPreview.videoPlayer.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                imagePreview.submissionImage.visibility = View.VISIBLE
                                Glide.with(imagePreview.submissionImage)
                                    .load(it.url)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(imagePreview.submissionImage)
                            }
                            is Link.VideoLink -> {
                                imagePreview.submissionImage.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                videoPreview.videoPlayer.visibility = View.VISIBLE
                                //videoPreview.videoPlayer.initialise(simpleExoPlayer, args.post.urlOverriddenByDest ?: "")
                            }
                            Link.RedditVideo -> {
                                imagePreview.submissionImage.visibility = View.GONE
                                externalLinkPreview.externalLinkContainer.visibility = View.GONE
                                videoPreview.videoPlayer.visibility = View.VISIBLE
                                Timber.d(args.post.media?.redditVideo?.dashUrl ?:
                                    args.post.media?.redditVideo?.fallbackUrl!!)
//                                player = videoPreview.videoPlayer.initialise(simpleExoPlayer,
//                                    args.post.media?.redditVideo?.dashUrl ?:
//                                    args.post.media?.redditVideo?.fallbackUrl!!)
                            }
                            is Link.TwitterLink -> {
                                // TODO: Get Twitter API access
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
                cs.clone(contentContainer)
                cs.connect(
                    textSubmissionAuthor.id,
                    ConstraintSet.START,
                    submissionPin.id,
                    ConstraintSet.END,
                    root.context.resources.getDimension(R.dimen.spacing_s).toInt())
                cs.applyTo(contentContainer)
            } else {
                textSubmissionAuthor.setTextColor(root.context.getColor(R.color.black))
                textSubmissionAuthor.setTypeface(textSubmissionAuthor.typeface, Typeface.NORMAL)
                submissionPin.visibility = View.GONE
                val cs = ConstraintSet()
                cs.clone(contentContainer)
                cs.connect(
                    textSubmissionAuthor.id,
                    ConstraintSet.START,
                    textSubredditName.id,
                    ConstraintSet.START)
                cs.applyTo(contentContainer)
            }
            if (args.post.authorFlairText.isNullOrBlank()) {
                textAuthorFlair.visibility = View.GONE
            } else {
                textAuthorFlair.visibility = View.VISIBLE
                textAuthorFlair.text = args.post.authorFlairText
                if (args.post.authorFlairBackgroundColor.isNullOrEmpty()) {
                    textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.LTGRAY)
                } else if (args.post.authorFlairBackgroundColor.equals("transparent", true)) {
                    textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
                } else {
                    textAuthorFlair.backgroundTintList = ColorStateList.valueOf(Color.parseColor(args.post.authorFlairBackgroundColor))
                }
            }
            buttonUpvoteAction.isSelected = args.post.likes ?: false
            buttonDownvoteAction.isSelected = !(args.post.likes ?: true)
            buttonUpvoteAction.setOnClickListener {
                // Send the upvote/removal
                if (it.isSelected) {
                    viewModel.onUiEvent(SubmissionDetailEvent.RemoveUpvote(args.post.name))
                } else {
                    viewModel.onUiEvent(SubmissionDetailEvent.Upvote(args.post.name))
                }
                it.isSelected = !it.isSelected
                if (buttonDownvoteAction.isSelected)
                    buttonDownvoteAction.isSelected = false
            }
            buttonDownvoteAction.setOnClickListener {
                // Send the upvote/removal
                if (it.isSelected) {
                    viewModel.onUiEvent(SubmissionDetailEvent.RemoveUpvote(args.post.name))
                } else {
                    viewModel.onUiEvent(SubmissionDetailEvent.Downvote(args.post.name))
                }
                it.isSelected = !it.isSelected
                if (buttonUpvoteAction.isSelected)
                    buttonUpvoteAction.isSelected = false
            }
        }

        observe(viewModel.voteState) {
            when (it) {
                is RedditResult.Error -> {
                    Timber.e(it.errorMessage)
                }
                RedditResult.UnAuthenticated -> {
                    binding.buttonUpvoteAction.isSelected = !binding.buttonUpvoteAction.isSelected
                    Toast.makeText(
                        requireContext(),
                        R.string.action_error_aunauthenticated,
                        Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        observe(viewModel.commentsTree) { comments ->
            when (comments) {
                is RedditResult.Error -> {
                    Timber.e(comments.errorMessage)
                }
                RedditResult.Loading -> {
                    // Do nothing for now
                }
                is RedditResult.Success -> {
                    if (comments.data.isNullOrEmpty()) {
                        binding.commentsTreeList.visibility = View.GONE
                        binding.emptyCommentEasterEgg.visibility = View.VISIBLE
                        binding.emptyCommentEasterEgg.text = requireContext().getEasterEggString(args.post.subreddit)
                    } else {
                        adapter?.submitList(comments.data.toMutableList())
                        binding.emptyCommentEasterEgg.visibility = View.GONE
                        binding.commentsTreeList.visibility = View.VISIBLE
                        binding.commentsTreeList.adapter = adapter
                    }
                    binding.loadingIndicator.visibility = View.GONE
                    loading.reset()
                }
                RedditResult.UnAuthenticated -> {
                    Toast.makeText(
                        requireContext(),
                        R.string.action_error_aunauthenticated,
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.getComments(args.post.id, "")
    }

    override fun onLoadMore(moreComments: MoreComments) {
        Timber.d("Load more")
        viewModel.getMoreComments(moreComments, args.post.name)
    }

    override fun onDestroyView() {
        player?.release()
        super.onDestroyView()
    }
}