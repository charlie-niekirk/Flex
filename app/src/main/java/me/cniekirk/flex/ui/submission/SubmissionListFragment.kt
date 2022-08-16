package me.cniekirk.flex.ui.submission

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import im.ene.toro.exoplayer.ExoCreator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.databinding.SubmissionListFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.SubmissionListAdapter
import me.cniekirk.flex.ui.adapter.SubmissionListLoadingStateAdapter
import me.cniekirk.flex.ui.util.onDialogResult
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding
import timber.log.Timber
import javax.inject.Inject

/**
 * [Fragment] displaying a list of submissions from any source i.e. MultiReddit, Subreddit, Home etc.
 *
 * @author Charlie Niekirk
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SubmissionListFragment
    : BaseFragment(R.layout.submission_list_fragment), SubmissionListAdapter.SubmissionActionListener {

    private val viewModel by viewModels<SubmissionListViewModel>()
    private val loading by lazy(LazyThreadSafetyMode.NONE) { binding.loadingIndicator.drawable as AnimatedVectorDrawable }
    private val binding by viewBinding(SubmissionListFragmentBinding::bind)
    private var adapter: SubmissionListAdapter? = null

    @Inject lateinit var exoCreator: ExoCreator

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bottom_navigation, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        if (!arguments?.getString("subreddit").isNullOrEmpty()) {
            viewModel.onUiEvent(SubmissionListEvent.SubredditUpdated(arguments?.getString("subreddit")!!))
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("selected_subreddit")?.observe(viewLifecycleOwner) { result ->
            Timber.d("RESULT: $result")
            if (result?.equals("random", true) == true ||
                result?.equals("randnsfw", true) == true) {
                viewModel.onUiEvent(SubmissionListEvent.RandomSubredditSelected(result))
            } else {
                viewModel.onUiEvent(SubmissionListEvent.SubredditUpdated(result))
            }
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>("selected_subreddit")
        }

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.VISIBLE
            bottomBar.visibility = View.VISIBLE
        }

        actionButton?.setOnClickListener {
            val action = SubmissionListFragmentDirections.actionSubmissionListFragmentToCreatePostFragment()
            findNavController().navigate(action)
        }

        observe(viewModel.settingsFlow) { settings ->
            if (settings.profilesCount == 0) {
                val dlgView = LayoutInflater.from(requireContext()).inflate(R.layout.analytics_dialog_view, null)
                // If first launch
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                    .setTitle(R.string.analytics_dialog_title)
                    .setIcon(R.drawable.ic_data_analytics)
                    .setView(dlgView)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        val crash = dlgView.findViewById<SwitchMaterial>(R.id.crashlytics_checkbox).isChecked
                        val analytics = dlgView.findViewById<SwitchMaterial>(R.id.analytics_checkbox).isChecked

                        Firebase.crashlytics.setCrashlyticsCollectionEnabled(crash)
                        Firebase.analytics.setAnalyticsCollectionEnabled(analytics)

                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.initialiseDefaultSettings()
                        }
                        dialog.dismiss()
                    }.show()
            } else {
                if (adapter == null) {
                    adapter = SubmissionListAdapter(this@SubmissionListFragment, settings, exoCreator)
                    adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                //binding.listSubmissions.setItemViewCacheSize(20)
                binding.listSubmissions.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                binding.listSubmissions.adapter = adapter?.withLoadStateFooter(
                    footer = SubmissionListLoadingStateAdapter()
                )
                // Collect paginated submissions and submit to adapter
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    adapter?.addLoadStateListener { loadState ->
                        if (loadState.refresh is LoadState.Loading) {
                            startLoadingAnimation()
                        }
                        if (loadState.refresh is LoadState.NotLoading &&
                            adapter?.itemCount!! > 0) {
                            stopLoadingAnimation()
                        }
                    }
                    viewModel.pagingSubmissionFlow.collectLatest {
                        adapter?.submitData(it)
                    }
                }
            }
        }

        onDialogResult<String>(R.id.submissionListFragment, "sort") { sort ->
            sort?.let { viewModel.onUiEvent(SubmissionListEvent.SortUpdated(it)) }
        }

        binding.submissionSort.setOnClickListener {
            it.findNavController().navigate(
                R.id.action_submissionListFragment_to_submissionListSortDialogFragment
            )
        }

        // Keep the sort UI updated
        observe(viewModel.sortFlow) {
            if (it.isNotEmpty() && it.length > 1) {
                binding.textSubmissionSortLabel.text =
                    it.substring(1).replaceFirstChar { char ->
                    char.uppercaseChar()
                }
            } else {
                binding.textSubmissionSortLabel.text = getString(R.string.sort_best)
            }
        }

        // Keep the submission source updated
        observe(viewModel.subredditFlow) {
            binding.textSubmissionSource.text = it
        }

        observe(viewModel.subredditInfo) {
            when (it) {
                is RedditResult.Error -> {

                }
                RedditResult.Loading -> { }
                is RedditResult.Success -> {
                    val action = SubmissionListFragmentDirections
                        .actionSubmissionListFragmentToSubredditInformationDialog(it.data)
                    findNavController().navigate(action)
                }
            }
        }

        binding.buttonSubredditOptions.setOnClickListener {
            viewModel.onUiEvent(SubmissionListEvent.SubredditOptions)
        }
    }

    private val callback = object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            binding.loadingIndicator.post { loading.start() }
        }
    }

    private fun startLoadingAnimation() {
        binding.loadingIndicator.visibility = View.VISIBLE
        loading.registerAnimationCallback(callback)
        loading.start()
    }

    private fun stopLoadingAnimation() {
        view?.let {
            binding.loadingIndicator.visibility = View.INVISIBLE
            loading.unregisterAnimationCallback(callback)
            loading.reset()
        }
    }

    override fun onDestroyView() {
        loading.unregisterAnimationCallback(callback)
        super.onDestroyView()
    }

    override fun onPostClicked(post: AuthedSubmission) {
        val action = SubmissionListFragmentDirections
            .actionSubmissionListFragmentToSubmissionDetailFragment(post)
        binding.root.findNavController().navigate(action)
    }

    override fun onPostLongClicked(post: AuthedSubmission) {
        val action = SubmissionListFragmentDirections
            .actionSubmissionListFragmentToSharePostAsImageDialog(post)
        binding.root.findNavController().navigate(action)
    }

    override fun onGalleryClicked(post: AuthedSubmission) {
        val action = SubmissionListFragmentDirections
            .actionSubmissionListFragmentToSlidingGalleryContainer(post, null)
        binding.root.findNavController().navigate(action)
    }

    override fun onYoutubeVideoClicked(videoId: String) {
        val action = SubmissionListFragmentDirections
            .actionSubmissionListFragmentToYoutubePlayer(videoId)
        binding.root.findNavController().navigate(action)
    }

    override fun onSubredditClicked(subreddit: String) {

    }

    override fun onUserClicked(username: String) {

    }

    override fun onPause() {
        viewModel.resetSubredditInfo()
        super.onPause()
    }
}