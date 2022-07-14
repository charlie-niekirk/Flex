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
import me.cniekirk.flex.ui.state.SettingsSideEffect
import me.cniekirk.flex.ui.state.SettingsState
import me.cniekirk.flex.ui.viewmodel.SettingsViewModel
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding
import org.orbitmvi.orbit.viewmodel.observe

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
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.VISIBLE
        }

        viewModel.observe(viewLifecycleOwner, ::render, ::react)

        binding.addProfileButton.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAddSettingsProfileFragment()
            binding.root.findNavController().navigate(action)
        }

        binding.nsfwBlurEnabled.setOnClickListener {
            viewModel.setBlurNsfw()
        }
    }

    private fun render(settingsState: SettingsState) {
        val settings = settingsState.settings

        if (!settings.profilesList.isNullOrEmpty()) {
            val adapter = SettingProfilesAdapter()
            binding.settingProfilesList.adapter = adapter
            adapter.submitList(settings.profilesList)

            val enabledProfile = settings.profilesList.first { it.selected }

            binding.nsfwBlurEnabled.isChecked = enabledProfile.blurNsfw
            binding.postPreviewsEnabled.isChecked = enabledProfile.showPreviews
        }
    }

    private fun react(settingsSideEffect: SettingsSideEffect) {

    }
}