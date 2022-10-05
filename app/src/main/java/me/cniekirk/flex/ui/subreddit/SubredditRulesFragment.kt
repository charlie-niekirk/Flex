package me.cniekirk.flex.ui.subreddit

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.linkify.LinkifyPlugin
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubredditRulesFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.SubredditRulesAdapter
import me.cniekirk.flex.ui.viewmodel.SubredditActionsViewModel
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding
import timber.log.Timber

@AndroidEntryPoint
class SubredditRulesFragment : BaseFragment(R.layout.subreddit_rules_fragment) {

    private val binding by viewBinding(SubredditRulesFragmentBinding::bind)
    private val viewModel by viewModels<SubredditActionsViewModel>()
    private val args by navArgs<SubredditRulesFragmentArgs>()
    private var adapter: SubredditRulesAdapter? = null
    private val markwon by lazy(LazyThreadSafetyMode.NONE) {
        Markwon
            .builder(requireContext())
            .usePlugin(StrikethroughPlugin())
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(TablePlugin.create(requireContext()))
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

        binding.apply {
            adapter = SubredditRulesAdapter(markwon)
            subredditRulesList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            subredditRulesList.adapter = adapter

            textRulesTitle.text = getString(R.string.rules_title_format, args.subreddit)

            observe(viewModel.rules) {
                when (it) {
                    is RedditResult.Success -> {
                        adapter?.submitList(it.data.rules)
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

            viewModel.getSubredditRules(args.subreddit)
        }
    }
}