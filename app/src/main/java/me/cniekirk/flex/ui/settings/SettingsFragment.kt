package me.cniekirk.flex.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SettingsFragmentBinding
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.SettingProfilesAdapter
import me.cniekirk.flex.ui.viewmodel.SettingsViewModel
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding

/**
 * Fragment to show the possible settings the user can change within the application
 *
 * @author Charlie Niekirk
 */
@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.settings_fragment) {

    private val binding by viewBinding(SettingsFragmentBinding::bind)
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.VISIBLE
            bottomBar.visibility = View.VISIBLE
        }

        observe(viewModel.settings) { settings ->
            val adapter = SettingProfilesAdapter()
            binding.settingProfilesList.adapter = adapter
            adapter.submitList(settings.profilesList)

            val enabledProfile = settings.profilesList.first { it.selected }

            binding.nsfwBlurEnabled.isChecked = enabledProfile.blurNsfw
            binding.postPreviewsEnabled.isChecked = enabledProfile.showPreviews
        }

        binding.addProfileButton.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAddSettingsProfileFragment()
            binding.root.findNavController().navigate(action)
        }

        binding.nsfwBlurEnabled.setOnClickListener {
            viewModel.setBlurNsfw()
        }
    }
}