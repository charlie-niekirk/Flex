package me.cniekirk.flex.ui.submission

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.AbstractListDetailFragment
import androidx.navigation.fragment.NavHostFragment
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
import me.cniekirk.flex.SubmissionDetailGraphDirections
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.databinding.SubmissionMasterDetailPaneBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.adapter.SubmissionListAdapter
import me.cniekirk.flex.ui.adapter.SubmissionListLoadingStateAdapter
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel
import me.cniekirk.flex.util.observe
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SubmissionTwoPaneFragment : AbstractListDetailFragment(), SubmissionListAdapter.SubmissionActionListener {

    private var binding: SubmissionMasterDetailPaneBinding? = null
    private val viewModel by activityViewModels<SubmissionListViewModel>()
    private val loading by lazy(LazyThreadSafetyMode.NONE) {
        binding?.listPane?.loadingIndicator?.drawable as AnimatedVectorDrawable
    }
    private var adapter: SubmissionListAdapter? = null

    @Inject
    lateinit var exoCreator: ExoCreator

    override fun onCreateListPaneView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.submission_master_detail_pane, container, false)
    }

    override fun onCreateDetailPaneNavHostFragment(): NavHostFragment {
        return NavHostFragment.create(R.navigation.submission_detail_graph)
    }

    override fun onListPaneViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onListPaneViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<View>(R.id.bottom_navigation)
        if (actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.VISIBLE
            bottomBar.visibility = View.VISIBLE
        }

        binding = SubmissionMasterDetailPaneBinding.bind(view)

        observe(viewModel.settingsFlow) { settings ->
            if (settings.profilesCount == 0) {
                val dlgView = LayoutInflater.from(requireContext()).inflate(R.layout.analytics_dialog_view, null)
                // If first launch
                MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                    .setTitle(R.string.analytics_dialog_title)
                    .setIcon(R.drawable.ic_data_analytics)
                    .setView(dlgView)
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        if (dlgView.findViewById<SwitchMaterial>(R.id.crashlytics_checkbox).isChecked) {
                            Firebase.crashlytics.setCrashlyticsCollectionEnabled(true)
                        } else if (dlgView.findViewById<SwitchMaterial>(R.id.analytics_checkbox).isChecked) {
                            Firebase.analytics.setAnalyticsCollectionEnabled(true)
                        }
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.initialiseDefaultSettings()
                        }
                        dialog.dismiss()
                    }.show()
            } else {
                if (adapter == null) {
                    adapter = SubmissionListAdapter(this@SubmissionTwoPaneFragment, settings, exoCreator)
                    adapter?.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                //binding.listSubmissions.setItemViewCacheSize(20)
                binding?.listPane?.listSubmissions?.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
                binding?.listPane?.listSubmissions?.adapter = adapter?.withLoadStateFooter(
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

        binding?.listPane?.submissionSort?.setOnClickListener {
            it.findNavController().navigate(
                R.id.action_submissionListFragment_to_submissionListSortDialogFragment
            )
        }

//        // Keep the sort UI updated
//        observe(viewModel.sortFlow) {
//            if (it.isNotEmpty() && it.length > 1) {
//                binding.textSubmissionSortLabel.text =
//                    it.substring(1).replaceFirstChar { char ->
//                        char.uppercaseChar()
//                    }
//            } else {
//                binding.textSubmissionSortLabel.text = getString(R.string.sort_best)
//            }
//        }

        // Keep the submission source updated
        observe(viewModel.subredditFlow) {
            binding?.listPane?.textSubmissionSource?.text = it
        }

        observe(viewModel.subredditInfo) {
            when (it) {
                is RedditResult.Error -> {
                    Timber.e(it.errorMessage)
                }
                RedditResult.Loading -> { }
                is RedditResult.Success -> {
//                    val action = SubmissionListFragmentDirections
//                        .actionSubmissionListFragmentToSubredditInformationDialog(it.data)
//                    findNavController().navigate(action)
                }
                RedditResult.UnAuthenticated -> {
                    Timber.e("Unauthenticated!")
                }
            }
        }
    }

    private val callback = object : Animatable2.AnimationCallback() {
        override fun onAnimationEnd(drawable: Drawable?) {
            binding?.listPane?.loadingIndicator?.post { loading.start() }
        }
    }

    private fun startLoadingAnimation() {
        binding?.listPane?.loadingIndicator?.visibility = View.VISIBLE
        loading.registerAnimationCallback(callback)
        loading.start()
    }

    private fun stopLoadingAnimation() {
        view?.let {
            binding?.listPane?.loadingIndicator?.visibility = View.INVISIBLE
            loading.unregisterAnimationCallback(callback)
            loading.reset()
        }
    }

    override fun onPostClicked(post: AuthedSubmission) {
        val detailNavController = childFragmentManager.findFragmentById(R.id.detail_pane)?.findNavController()
        detailNavController?.navigate(SubmissionDetailGraphDirections.toSubmissionDetailFragment(post))
        if (binding?.slidingPaneLayout?.isSlideable == true) {
            binding?.slidingPaneLayout?.openPane()
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding?.slidingPaneLayout?.isSlideable == false) {
            binding?.slidingPaneLayout?.closePane()
        }
    }

    override fun onPostLongClicked(post: AuthedSubmission) {
    }

    override fun onGalleryClicked(post: AuthedSubmission) {
    }

    override fun onYoutubeVideoClicked(videoId: String) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loading.unregisterAnimationCallback(callback)
        binding = null
    }
}