package me.cniekirk.flex.ui.submission

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubmissionListFragmentBinding
import me.cniekirk.flex.ui.adapter.SubmissionListAdapter
import me.cniekirk.flex.util.observe

/**
 * [Fragment] displaying a list of submissions from any source i.e. MultiReddit, Subreddit, Home etc.
 *
 * @author Charlie Niekirk
 */
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SubmissionListFragment : Fragment(R.layout.submission_list_fragment) {

    // Shared VM with the sort dialog fragment
    private val viewModel by activityViewModels<SubmissionListViewModel>()
    private var binding: SubmissionListFragmentBinding? = null
    private var adapter: SubmissionListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SubmissionListFragmentBinding.bind(view)

        binding?.apply {
            adapter = SubmissionListAdapter()
            listSubmissions.adapter = adapter
            submissionSort.setOnClickListener {
                it.findNavController().navigate(
                    R.id.action_submissionListFragment_to_submissionListSortDialogFragment
                )
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

        // Collect paginated submissions and submit to adapter
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.pagingSubmissionFlow.collectLatest {
                adapter?.submitData(it)
            }
        }
    }
}