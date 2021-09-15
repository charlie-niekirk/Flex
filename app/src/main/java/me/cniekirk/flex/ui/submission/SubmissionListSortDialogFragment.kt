package me.cniekirk.flex.ui.submission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.databinding.SubmissionListSortDialogBinding
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel

@AndroidEntryPoint
class SubmissionListSortDialogFragment : BottomSheetDialogFragment() {

    private val viewModel by activityViewModels<SubmissionListViewModel>()
    private var binding: SubmissionListSortDialogBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SubmissionListSortDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            sortTop.setOnClickListener {
                handleSelection("top")
            }
            sortBest.setOnClickListener {
                handleSelection("")
            }
            sortNew.setOnClickListener {
                handleSelection("new")
            }
            sortHot.setOnClickListener {
                handleSelection("hot")
            }
        }
    }

    private fun handleSelection(sort: String) {
        viewModel.onUiEvent(SubmissionListEvent.SortUpdated(sort))
        dismiss()
    }
}