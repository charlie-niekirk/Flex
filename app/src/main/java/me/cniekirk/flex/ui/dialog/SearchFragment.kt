package me.cniekirk.flex.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SearchFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.SubredditResultAdapter
import me.cniekirk.flex.ui.submission.SubmissionListEvent
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel
import me.cniekirk.flex.util.textChanges
import me.cniekirk.flex.util.viewBinding
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.search_fragment), SubredditResultAdapter.SubredditResultListener {

    private val binding by viewBinding(SearchFragmentBinding::bind)
    private val viewModel by activityViewModels<SubmissionListViewModel>()
    private val adapter = SubredditResultAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            subredditResultList.addItemDecoration(DividerItemDecoration(
                requireContext(), LinearLayoutManager.VERTICAL
            ))
            subredditResultList.adapter = adapter
            randomNsfwContainer.clipToOutline = true
            randomSubContainer.clipToOutline = true

            randomSubContainer.setOnClickListener {
                viewModel.onUiEvent(SubmissionListEvent.RandomSubredditSelected("random"))
                binding.root.findNavController().popBackStack()
            }
            randomNsfwContainer.setOnClickListener {
                viewModel.onUiEvent(SubmissionListEvent.RandomSubredditSelected("randnsfw"))
                binding.root.findNavController().popBackStack()
            }

            inputSearch.textChanges()
                .filterNot { it.isNullOrBlank() }
                .debounce(300)
                .flatMapLatest { viewModel.searchSubreddit(it.toString()) }
                .onEach {
                    when (it) {
                        is RedditResult.Success -> {
                            adapter.submitList(it.data) {
                                subredditResultList.scrollToPosition(0)
                            }
                        }
                        is RedditResult.Error -> {
                            // Display error
                            Timber.e(it.errorMessage)
                        }
                        else -> {
                            // Display unknown error
                            Timber.e("Unknown error!")
                        }
                    }
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    override fun onSubredditSelected(subreddit: String) {
        viewModel.onUiEvent(SubmissionListEvent.SubredditUpdated(subreddit))
        binding.root.findNavController().popBackStack()
    }
}