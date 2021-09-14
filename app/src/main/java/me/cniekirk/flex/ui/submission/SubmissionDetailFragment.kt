package me.cniekirk.flex.ui.submission

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SubmissionDetailFragmentBinding

class SubmissionDetailFragment : Fragment(R.layout.submission_detail_fragment) {

    //private val args by navArgs<>()
    private var binding: SubmissionDetailFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SubmissionDetailFragmentBinding.bind(view)

        binding?.apply {

        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}