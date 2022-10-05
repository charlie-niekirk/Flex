package me.cniekirk.flex.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.databinding.SubmissionListSortDialogBinding
import me.cniekirk.flex.ui.submission.SubmissionListEvent
import me.cniekirk.flex.ui.util.setResult
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel
import me.cniekirk.flex.util.setCurrentScreen

@AndroidEntryPoint
class SubmissionListSortDialogFragment : BottomSheetDialogFragment() {

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
        setResult("sort", sort)
        dismiss()
    }

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }
}