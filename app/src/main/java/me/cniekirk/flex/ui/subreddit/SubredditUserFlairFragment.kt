package me.cniekirk.flex.ui.subreddit

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.SlideDistanceProvider
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.flair.UserFlairItem
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
            textFlairTitle.text = getString(R.string.flair_title_format, args.subreddit)
            subredditFlairList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            subredditFlairList.adapter = adapter

            observe(viewModel.userFlairs) {
                when (it) {
                    is RedditResult.Error -> {
                        Timber.e(it.errorMessage)
                    }
                    RedditResult.Loading -> {}
                    is RedditResult.Success -> {
                        adapter.submitList(it.data)
                    }
                    RedditResult.UnAuthenticated -> {
                        Toast.makeText(requireContext(), R.string.action_error_aunauthenticated, Toast.LENGTH_SHORT).show()
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