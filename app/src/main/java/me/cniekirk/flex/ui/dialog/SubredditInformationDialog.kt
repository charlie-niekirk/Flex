package me.cniekirk.flex.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubredditDialogBinding
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel

@AndroidEntryPoint
class SubredditInformationDialog : BottomSheetDialogFragment() {

    private var binding: SubredditDialogBinding? = null
    private val viewModel by activityViewModels<SubmissionListViewModel>()

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

        binding?.apply {
            dialogTitle.text = root.context.getString(R.string.subreddit_dialog_title, viewModel.subredditFlow.value)
            actionFavourite.clipToOutline = true
            actionSetUserFlair.clipToOutline = true
            actionShowModerators.clipToOutline = true
            actionShowRules.clipToOutline = true
            actionShowSidebar.clipToOutline = true
            actionSubscribe.clipToOutline = true

            actionShowRules.setOnClickListener {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("rules", "rules")
                dismiss()
            }
            actionShowSidebar.setOnClickListener {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("sidebar", "sidebar")
                dismiss()
            }
            actionShowModerators.setOnClickListener {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("moderators", "moderators")
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}