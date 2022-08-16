package me.cniekirk.flex.ui.submission

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import im.ene.toro.exoplayer.ExoCreator
import io.noties.markwon.*
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.table.TableEntryPlugin
import io.noties.markwon.utils.Dip
import me.cniekirk.flex.R
import me.cniekirk.flex.data.Cause
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.databinding.SubmissionDetailFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.CommentTreeAdapter
import me.cniekirk.flex.ui.adapter.SubmissionDetailHeaderAdapter
import me.cniekirk.flex.ui.submission.state.SubmissionDetailEffect
import me.cniekirk.flex.ui.submission.state.SubmissionDetailState
import me.cniekirk.flex.ui.text.FlexLinkifyPlugin
import me.cniekirk.flex.ui.text.RedditLinkifyTextAddedListener
import me.cniekirk.flex.ui.viewmodel.SubmissionDetailViewModel
import me.cniekirk.flex.util.*
import org.orbitmvi.orbit.viewmodel.observe
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class SubmissionDetailFragment : BaseFragment(R.layout.submission_detail_fragment), CommentTreeAdapter.CommentActionListener, SubmissionDetailHeaderAdapter.SubmissionActionListener {

    private var player: ExoPlayer? = null
    private val args by navArgs<SubmissionDetailFragmentArgs>()
    private val loading by lazy(LazyThreadSafetyMode.NONE) { binding.loadingIndicator.drawable as AnimatedVectorDrawable }
    private val viewModel by viewModels<SubmissionDetailViewModel>()
    private val binding by viewBinding(SubmissionDetailFragmentBinding::bind)
    private val smoothScroller: SmoothScroller by lazy(LazyThreadSafetyMode.NONE) {
        object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_START
            }
        }
    }
    private var adapter: CommentTreeAdapter? = null
    private var headerAdapter: SubmissionDetailHeaderAdapter? = null

    private val markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon.builder(requireContext())
            .usePlugin(StrikethroughPlugin())
            .usePlugin(FlexLinkifyPlugin.create())
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configure(registry: MarkwonPlugin.Registry) {
                    registry.require(CorePlugin::class.java) { corePlugin ->
                        corePlugin.addOnTextAddedListener(RedditLinkifyTextAddedListener())
                    }
                }
            })
            .usePlugin(TableEntryPlugin.create { builder ->
                val dip = Dip.create(requireContext())
                builder
                    .tableBorderColor(
                        requireContext().resources.getColor(
                            R.color.table_border,
                            null
                        )
                    )
                    .tableHeaderRowBackgroundColor(
                        requireContext().resources.getColor(
                            R.color.table_border,
                            null
                        )
                    )
                    .tableCellPadding(dip.toPx(4))
                    .tableBorderWidth(dip.toPx(1))
                    .build()
            })
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
                    builder.linkResolver(object : LinkResolverDef() {
                        override fun resolve(view: View, link: String) {
                            link.processLink {
                                when (it) {
                                    is Link.ImgurGalleryLink -> {
                                        val action = SubmissionDetailFragmentDirections
                                            .actionSubmissionDetailFragmentToSlidingGalleryContainer(
                                                it.albumId,
                                                args.post!!
                                            )
                                        findNavController().navigate(action)
                                    }
                                    else -> {
                                        if (link.startsWith(requireContext().getString(R.string.subreddit_link_prefix))) {
                                            // Go to subreddit
                                            val navOptions = NavOptions.Builder()
                                                    .setEnterAnim(R.anim.fragment_open_enter)
                                                    .setExitAnim(R.anim.fragment_open_exit)
                                                    .setPopEnterAnim(R.anim.fragment_close_enter)
                                                    .setPopExitAnim(R.anim.fragment_close_exit)
                                                    .build()
                                            findNavController().navigate(Uri.parse(link), navOptions)
                                        } else {
                                            super.resolve(view, link)
                                        }
                                    }
                                }
                            }
                        }
                    })
                }
            })
            .build()
    }

    @Inject lateinit var markwonAdapter: MarkwonAdapter
    @Inject lateinit var exoCreator: ExoCreator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observe(viewLifecycleOwner, ::render, ::react)

        args.post?.let {
            viewModel.getComments(it, "")
        }
    }

    private fun render(state: SubmissionDetailState) {

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }

        binding.apply {
            backButton.setOnClickListener { it.findNavController().popBackStack() }

            textCommentsTitle.text = getString(R.string.comments_title, args.post?.numComments?.condense())
            loading.registerAnimationCallback(object : Animatable2.AnimationCallback() {
                override fun onAnimationEnd(drawable: Drawable?) {
                    loadingIndicator.post { loading.start() }
                }
            })
            loading.start()

            args.post?.let {
                headerAdapter = SubmissionDetailHeaderAdapter(this@SubmissionDetailFragment, null, exoCreator, markwon, markwonAdapter)
                adapter = CommentTreeAdapter(it, markwon, this@SubmissionDetailFragment)
                commentsTreeList.adapter = ConcatAdapter(headerAdapter, adapter)
                headerAdapter!!.submitList(listOf(args.post))

                nextTopCommentButton.setOnClickListener {
                    val start = (commentsTreeList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() - 1
                    val nextItem = adapter?.currentList?.filterIndexed { index, item ->
                        index > start && item.depth == 0
                    }?.first()
                    smoothScroller.targetPosition = adapter?.currentList?.indexOf(nextItem)?.plus(1) ?: 0
                    commentsTreeList.layoutManager?.startSmoothScroll(smoothScroller)
                }
            }

            if (state.comments.isEmpty()) {
                binding.emptyCommentEasterEgg.visibility = View.VISIBLE
                binding.emptyCommentEasterEgg.text = requireContext().getEasterEggString(args.post!!.subreddit)
            } else {
                adapter?.submitList(state.comments)
                binding.emptyCommentEasterEgg.visibility = View.GONE
                binding.commentsTreeList.visibility = View.VISIBLE
            }

            binding.loadingIndicator.visibility = View.GONE
            loading.reset()
        }
    }

    private fun react(effect: SubmissionDetailEffect) {
        when (effect) {
            is SubmissionDetailEffect.ShowError -> {
                Toast.makeText(requireContext(), effect.message, Toast.LENGTH_SHORT).show()
            }
            is SubmissionDetailEffect.UpdateVoteState -> {
                headerAdapter?.submitList(listOf(args.post?.copy(voteState = effect.voteState)))
            }
        }
    }

    override fun onLoadMore(moreComments: MoreComments) {
        viewModel.getMoreComments(moreComments, args.post!!.name)
    }

    override fun onReply(comment: Comment) {
        val action = SubmissionDetailFragmentDirections
            .actionSubmissionDetailFragmentToComposeCommentFragment(comment.copy(replies = emptyList(), repliesRaw = null))
        findNavController().navigate(action)
    }

    override fun onImgurGalleryClicked(albumId: String) {
        val action = SubmissionDetailFragmentDirections
            .actionSubmissionDetailFragmentToSlidingGalleryContainer(albumId, args.post!!)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        player?.release()
        super.onDestroyView()
    }

    override fun onGalleryClicked(post: AuthedSubmission) {
        val action = if (post.imgurGalleryLinks.isNullOrEmpty() || post.imgurGalleryLinks!!.size < 2) {
            SubmissionDetailFragmentDirections.actionSubmissionDetailFragmentToSlidingGalleryContainer(null, post)
        } else {
            //SubmissionDetailFragmentDirections.actionSubmissionDetailFragmentToSlidingGalleryContainer(, null)
        }
    }

    override fun onImageClicked(post: AuthedSubmission) {
        TODO("Not yet implemented")
    }

    override fun onVideoClicked(post: AuthedSubmission) {
        TODO("Not yet implemented")
    }

    override fun onYoutubeVideoClicked(videoId: String) {
        val action = SubmissionDetailFragmentDirections
            .actionSubmissionDetailFragmentToYoutubePlayer(videoId)
        binding.root.findNavController().navigate(action)
    }

    override fun onUpvoteClicked(thingId: String) {
        viewModel.upvoteClicked(thingId)
    }

    override fun onDownvoteClicked(thingId: String) {
        viewModel.downvoteClicked(thingId)
    }

    override fun onLinkClicked(post: AuthedSubmission) {

    }
}