package me.cniekirk.flex.ui.subreddit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.SlideDistanceProvider
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubredditModeratorsFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.SubredditModeratorsAdapter
import me.cniekirk.flex.ui.adapter.SubredditRulesAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            (primaryAnimatorProvider as SlideDistanceProvider).slideDistance =
                requireContext().resources.getDimension(R.dimen.slide_distance).toInt()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            (primaryAnimatorProvider as SlideDistanceProvider).slideDistance =
                requireContext().resources.getDimension(R.dimen.slide_distance).toInt()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottom_app_bar)
        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.hide()
            bottomAppBar.performHide()
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
                        Timber.e(it.errorMessage)
                    }
                    RedditResult.UnAuthenticated -> {
                        Toast.makeText(requireContext(), "Unauthenticated!", Toast.LENGTH_SHORT).show()
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