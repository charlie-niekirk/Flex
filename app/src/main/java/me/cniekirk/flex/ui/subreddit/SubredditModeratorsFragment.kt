package me.cniekirk.flex.ui.subreddit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubredditModeratorsFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.SubredditModeratorsAdapter
import me.cniekirk.flex.ui.viewmodel.SubredditActionsViewModel
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding
import timber.log.Timber

@AndroidEntryPoint
class SubredditModeratorsFragment : BaseFragment(R.layout.subreddit_moderators_fragment) {

    private val binding by viewBinding(SubredditModeratorsFragmentBinding::bind)
    private val viewModel by viewModels<SubredditActionsViewModel>()
    private val args by navArgs<SubredditModeratorsFragmentArgs>()
    private val adapter = SubredditModeratorsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }

        binding.apply {
            subredditModeratorsList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            subredditModeratorsList.adapter = adapter

            textModeratorsTitle.text = getString(R.string.moderators_title_format, args.subreddit)

            observe(viewModel.moderators) {
                when (it) {
                    is RedditResult.Success -> {
                        adapter.submitList(it.data)
                    }
                    RedditResult.Loading -> {
                        // Show spinner
                    }
                    is RedditResult.Error -> {
                    }
                }
            }

            backButton.setOnClickListener {
                findNavController().navigateUp()
            }

            viewModel.getSubredditModerators(args.subreddit)
        }
    }

}