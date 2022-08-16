package me.cniekirk.flex.ui.subreddit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.flair.UserFlairItem
import me.cniekirk.flex.databinding.SubredditUserFlairSelectionFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.SubredditUserFlairAdapter
import me.cniekirk.flex.ui.viewmodel.SubredditActionsViewModel
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding
import timber.log.Timber

@AndroidEntryPoint
class SubredditUserFlairFragment : BaseFragment(R.layout.subreddit_user_flair_selection_fragment),
    SubredditUserFlairAdapter.FlairClickListener {

    private val binding by viewBinding(SubredditUserFlairSelectionFragmentBinding::bind)
    private val viewModel by viewModels<SubredditActionsViewModel>()
    private val args by navArgs<SubredditUserFlairFragmentArgs>()
    private val adapter = SubredditUserFlairAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }

        binding.apply {
            textFlairTitle.text = getString(R.string.flair_title_format, args.subreddit)
            subredditFlairList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            subredditFlairList.adapter = adapter

            observe(viewModel.userFlairs) {
                when (it) {
                    is RedditResult.Error -> {
                    }
                    RedditResult.Loading -> {}
                    is RedditResult.Success -> {
                        adapter.submitList(it.data)
                    }
                }
            }

            viewModel.getAvailableFlair(args.subreddit)
        }
    }

    override fun onFlairClicked(flair: UserFlairItem) {
        if (flair.textEditable == true) {
            // Allow custom text to be entered
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.text_set_flair_title)
                .setView(R.layout.user_flair_alert_dialog)
                .setPositiveButton(R.string.ok) { di, _ ->
                    // Set the flair
                    di.dismiss()
                }.show()
        } else {
            // Just select the flair
        }
    }
}