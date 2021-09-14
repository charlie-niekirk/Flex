package me.cniekirk.flex.ui.submission

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.T3
import me.cniekirk.flex.databinding.SubmissionListFragmentBinding
import me.cniekirk.flex.ui.adapter.SubmissionListAdapter
import me.cniekirk.flex.ui.adapter.SubmissionListLoadingStateAdapter
import me.cniekirk.flex.util.observe
import timber.log.Timber

/**
 * [Fragment] displaying a list of submissions from any source i.e. MultiReddit, Subreddit, Home etc.
 *
 * @author Charlie Niekirk
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SubmissionListFragment
    : Fragment(R.layout.submission_list_fragment), SubmissionListAdapter.SubmissionActionListener {

    // Shared VM with the sort dialog fragment
    private val viewModel by activityViewModels<SubmissionListViewModel>()
    private var binding: SubmissionListFragmentBinding? = null
    private var adapter: SubmissionListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SubmissionListFragmentBinding.bind(view)

        binding?.apply {
            adapter = SubmissionListAdapter(this@SubmissionListFragment)
            listSubmissions.adapter = adapter?.withLoadStateFooter(
                footer = SubmissionListLoadingStateAdapter()
            )
            submissionSort.setOnClickListener {
                it.findNavController().navigate(
                    R.id.action_submissionListFragment_to_submissionListSortDialogFragment
                )
            }
        }

        // Collect paginated submissions and submit to adapter
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            adapter?.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.NotLoading &&
                        adapter?.itemCount!! > 0) {
                    binding?.loadingIndicator?.visibility = View.GONE
                }
            }
            viewModel.pagingSubmissionFlow.collectLatest {
                adapter?.submitData(it)
            }
        }

        // Keep the sort UI updated
        observe(viewModel.sortFlow) {
            if (it.isNotEmpty() && it.length > 1) {
                binding?.textSubmissionSortLabel?.text =
                    it.substring(1).replaceFirstChar { char ->
                    char.uppercaseChar()
                }
            } else {
                binding?.textSubmissionSortLabel?.text = getString(R.string.sort_best)
            }
        }

        // Keep the submission source updated
        observe(viewModel.subredditFlow) {
            binding?.textSubmissionSource?.text = it
        }
    }

    override fun onPause() {
        adapter?.dispose()
        super.onPause()
    }

    override fun onPostClicked(post: T3) {
        val action = SubmissionListFragmentDirections
            .actionSubmissionListFragmentToSubmissionDetailFragment(post)
        binding?.root?.findNavController()?.navigate(action)
    }
}