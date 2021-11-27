package me.cniekirk.flex.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubredditDialogBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.viewmodel.SubredditActionsViewModel
import me.cniekirk.flex.util.observe
import timber.log.Timber

@AndroidEntryPoint
class SubredditInformationDialog : BottomSheetDialogFragment() {

    private var binding: SubredditDialogBinding? = null
    private val viewModel by viewModels<SubredditActionsViewModel>()
    private val args by navArgs<SubredditInformationDialogArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SubredditDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observe(viewModel.singleActionState) {
            when (it) {
                is RedditResult.Error -> {
                    Timber.e(it.errorMessage)
                    dismiss()
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT)
                        .show()
                    dismiss()
                }
                RedditResult.UnAuthenticated -> {
                    Timber.e("Unauthenticated!")
                    dismiss()
                }
            }
        }

        binding?.apply {
            if (args.subreddit.userIsSubscriber == true) {
                actionSubscribe.text = getString(R.string.unsubscribe)
                actionSubscribe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_heart_break, 0, 0)
            } else {
                actionSubscribe.text = getString(R.string.subscribe)
                actionSubscribe.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_subscribe, 0, 0)
            }
            if (args.subreddit.userHasFavorited == true) {
                actionFavourite.text = getString(R.string.unfavourite)
                actionFavourite.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_unfavourite, 0, 0)
            } else {
                actionFavourite.text = getString(R.string.favourite)
                actionFavourite.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favourite, 0, 0)
            }
            dialogTitle.text = root.context.getString(R.string.subreddit_dialog_title, args.subreddit.displayName)
            actionFavourite.clipToOutline = true
            actionSetUserFlair.clipToOutline = true
            actionShowModerators.clipToOutline = true
            actionShowRules.clipToOutline = true
            actionShowSidebar.clipToOutline = true
            actionSubscribe.clipToOutline = true

            actionShowRules.setOnClickListener {
                val action = SubredditInformationDialogDirections
                    .actionSubredditInformationDialogToSubredditRulesFragment(args.subreddit.displayName ?: "")
                findNavController().navigate(action)
            }
            actionShowSidebar.setOnClickListener {
                val action = SubredditInformationDialogDirections
                    .actionSubredditInformationDialogToSubredditSidebarFragment(args.subreddit.displayName ?: "")
                findNavController().navigate(action)
            }
            actionShowModerators.setOnClickListener {
                val action = SubredditInformationDialogDirections
                    .actionSubredditInformationDialogToSubredditModeratorsFragment(args.subreddit.displayName ?: "")
                findNavController().navigate(action)
            }
            actionSetUserFlair.setOnClickListener {
                val action = SubredditInformationDialogDirections
                    .actionSubredditInformationDialogToSubredditUserFlairFragment(args.subreddit.displayName ?: "")
                findNavController().navigate(action)
            }
            actionSubscribe.setOnClickListener {
                viewModel.subscribeSubreddit(args.subreddit.name!!, args.subreddit.userIsSubscriber!!)
            }
            actionFavourite.setOnClickListener {
                viewModel.favoriteSubreddit(args.subreddit.displayName!!, args.subreddit.userHasFavorited!!)
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}