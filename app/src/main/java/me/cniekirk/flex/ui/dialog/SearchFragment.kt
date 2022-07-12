package me.cniekirk.flex.ui.dialog

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private val viewModel by viewModels<SubmissionListViewModel>()
    private val adapter = SubredditResultAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
        }

        binding.apply {
            subredditResultList.addItemDecoration(DividerItemDecoration(
                requireContext(), LinearLayoutManager.VERTICAL
            ))
            subredditResultList.adapter = adapter

            randomButton.setOnClickListener {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.fragment_open_enter)
                    .setExitAnim(R.anim.fragment_open_exit)
                    .setPopEnterAnim(R.anim.fragment_close_enter)
                    .setPopExitAnim(R.anim.fragment_close_exit)
                    .build()
                findNavController().navigate(Uri.parse(getString(R.string.subreddit_link_prefix) + "random"), navOptions)
            }
            randomNsfwButton.setOnClickListener {
                val navOptions = NavOptions.Builder()
                    .setEnterAnim(R.anim.fragment_open_enter)
                    .setExitAnim(R.anim.fragment_open_exit)
                    .setPopEnterAnim(R.anim.fragment_close_enter)
                    .setPopExitAnim(R.anim.fragment_close_exit)
                    .build()
                findNavController().navigate(Uri.parse(getString(R.string.subreddit_link_prefix) + "randnsfw"), navOptions)
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
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.fragment_open_enter)
            .setExitAnim(R.anim.fragment_open_exit)
            .setPopEnterAnim(R.anim.fragment_close_enter)
            .setPopExitAnim(R.anim.fragment_close_exit)
            .build()
        findNavController().navigate(Uri.parse(getString(R.string.subreddit_link_prefix) + subreddit), navOptions)
    }
}