package me.cniekirk.flex.ui.subreddit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubredditSidebarFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.viewmodel.SubredditActionsViewModel
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding
import timber.log.Timber

@AndroidEntryPoint
class SubredditSidebarFragment : BaseFragment(R.layout.subreddit_sidebar_fragment) {

    private val binding by viewBinding(SubredditSidebarFragmentBinding::bind)
    private val viewModel by viewModels<SubredditActionsViewModel>()
    private val args by navArgs<SubredditSidebarFragmentArgs>()
    private val markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon
            .builder(requireContext())
            .usePlugin(StrikethroughPlugin())
            .usePlugin(LinkifyPlugin.create())
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }

        binding.textSidebarTitle.text = getString(R.string.sidebar_title_format, args.subreddit)
        binding.backButton.setOnClickListener { findNavController().navigateUp() }

        observe(viewModel.info) {
            when (it) {
                is RedditResult.Success -> {
                    markwon.setMarkdown(binding.textSidebar, it.data.description ?: "")
                }
                else -> {
                    Timber.e("Error occurred")
                }
            }
        }

        viewModel.getSubredditInfo(args.subreddit)
    }

}