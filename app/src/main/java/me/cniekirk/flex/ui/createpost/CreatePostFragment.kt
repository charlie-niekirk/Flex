package me.cniekirk.flex.ui.createpost

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.CreatePostFragmentBinding
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.util.viewBinding

@AndroidEntryPoint
class CreatePostFragment : BaseFragment(R.layout.create_post_fragment) {

    private val binding by viewBinding(CreatePostFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }

        binding.closeButton.setOnClickListener { findNavController().popBackStack() }
    }
}