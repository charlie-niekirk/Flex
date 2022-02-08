package me.cniekirk.flex.ui.submission

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import im.ene.toro.exoplayer.ExoCreator
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.table.TableEntry
import io.noties.markwon.recycler.table.TableEntryPlugin
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.databinding.SubmissionDetailFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.CommentTreeAdapter
import me.cniekirk.flex.ui.adapter.SubmissionDetailHeaderAdapter
import me.cniekirk.flex.ui.viewmodel.SubmissionDetailViewModel
import me.cniekirk.flex.util.condense
import me.cniekirk.flex.util.getEasterEggString
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding
import org.commonmark.ext.gfm.tables.TableBlock
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

    @Inject lateinit var markwon: Markwon
    @Inject lateinit var markwonAdapter: MarkwonAdapter
    @Inject lateinit var exoCreator: ExoCreator

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.hide()
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

            val headerAdapter = SubmissionDetailHeaderAdapter(this@SubmissionDetailFragment, null, exoCreator, markwon, markwonAdapter)
            adapter = CommentTreeAdapter(args.post, markwon, this@SubmissionDetailFragment)
            commentsTreeList.adapter = ConcatAdapter(headerAdapter, adapter)
            headerAdapter.submitList(listOf(args.post))

            nextTopCommentButton.setOnClickListener {
                val start = (commentsTreeList.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() - 1
                val nextItem = adapter?.currentList?.filterIndexed { index, item ->
                    index > start && item.depth == 0
                }?.first()
                smoothScroller.targetPosition = adapter?.currentList?.indexOf(nextItem)?.plus(1) ?: 0
                commentsTreeList.layoutManager?.startSmoothScroll(smoothScroller)
            }
        }

        observe(viewModel.voteState) {
            when (it) {
                is RedditResult.Error -> {
                    Timber.e(it.errorMessage)
                }
                RedditResult.UnAuthenticated -> {
                    //binding.buttonUpvoteAction.isSelected = !binding.buttonUpvoteAction.isSelected
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
                        binding.emptyCommentEasterEgg.visibility = View.VISIBLE
                        binding.emptyCommentEasterEgg.text = requireContext().getEasterEggString(args.post.subreddit)
                    } else {
                        adapter?.submitList(comments.data)
                        binding.emptyCommentEasterEgg.visibility = View.GONE
                        binding.commentsTreeList.visibility = View.VISIBLE
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
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        val anim: Animation = loadAnimation(activity, nextAnim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                if (enter) {
                    viewModel.getComments(args.post, "")
                }
            }
        })
        return anim
    }

    override fun onLoadMore(moreComments: MoreComments) {
        viewModel.getMoreComments(moreComments, args.post.name)
    }

    override fun onReply(comment: Comment) {
        val action = SubmissionDetailFragmentDirections
            .actionSubmissionDetailFragmentToComposeCommentFragment(comment.copy(replies = emptyList(), repliesRaw = null))
        findNavController().navigate(action)
    }

    override fun onImgurGalleryClicked(albumId: String) {
        Timber.d("ALBUM: $albumId")
        val action = SubmissionDetailFragmentDirections
            .actionSubmissionDetailFragmentToSlidingGalleryContainer(albumId, args.post)
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

    override fun onLinkClicked(post: AuthedSubmission) {

    }
}