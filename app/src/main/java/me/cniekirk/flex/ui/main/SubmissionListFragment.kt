package me.cniekirk.flex.ui.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.cniekirk.flex.R

class SubmissionListFragment : Fragment() {

    companion object {
        fun newInstance() = SubmissionListFragment()
    }

    private lateinit var viewModel: SubmissionListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.submission_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SubmissionListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}