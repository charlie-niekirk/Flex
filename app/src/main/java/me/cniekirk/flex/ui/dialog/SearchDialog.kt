package me.cniekirk.flex.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import me.cniekirk.flex.databinding.SearchDialogBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.adapter.SubredditResultAdapter
import me.cniekirk.flex.ui.submission.SubmissionListEvent
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel
import me.cniekirk.flex.util.textChanges
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchDialog : FullscreenDialog(), SubredditResultAdapter.SubredditResultListener {

    private var binding: SearchDialogBinding? = null
    private val viewModel by activityViewModels<SubmissionListViewModel>()
    private val adapter = SubredditResultAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            subredditResultList.addItemDecoration(DividerItemDecoration(
                requireContext(), LinearLayoutManager.VERTICAL
            ))
            subredditResultList.adapter = adapter
            randomNsfwContainer.clipToOutline = true
            randomSubContainer.clipToOutline = true

            randomSubContainer.setOnClickListener {
                viewModel.onUiEvent(SubmissionListEvent.RandomSubredditSelected("random"))
                dismiss()
            }
            randomNsfwContainer.setOnClickListener {
                viewModel.onUiEvent(SubmissionListEvent.RandomSubredditSelected("randnsfw"))
                dismiss()
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

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onSubredditSelected(subreddit: String) {
        viewModel.onUiEvent(SubmissionListEvent.SubredditUpdated(subreddit))
        dismiss()
    }
}