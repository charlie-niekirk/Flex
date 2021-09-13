package me.cniekirk.flex.ui.submission

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubmissionListFragmentBinding
import me.cniekirk.flex.ui.adapter.SubmissionListAdapter

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SubmissionListFragment : Fragment(R.layout.submission_list_fragment) {

    private val viewModel by viewModels<SubmissionListViewModel>()
    private var binding: SubmissionListFragmentBinding? = null
    private var adapter: SubmissionListAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SubmissionListFragmentBinding.bind(view)

        binding?.apply {
            adapter = SubmissionListAdapter()
            listSubmissions.adapter = adapter
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.pagingSubmissionFlow.collectLatest {
                adapter?.submitData(it)
            }
        }

    }

}