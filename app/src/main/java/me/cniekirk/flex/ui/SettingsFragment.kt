package me.cniekirk.flex.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialSharedAxis
import com.google.android.material.transition.SlideDistanceProvider
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SettingsFragmentBinding
import me.cniekirk.flex.ui.adapter.SettingProfilesAdapter
import me.cniekirk.flex.ui.viewmodel.SettingsViewModel
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.viewBinding

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.settings_fragment) {

    private val binding by viewBinding(SettingsFragmentBinding::bind)
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottom_app_bar)
        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.hide()
            bottomAppBar.performHide()
        }

        observe(viewModel.settings) { settings ->
            val adapter = SettingProfilesAdapter()
            binding.settingProfilesList.adapter = adapter
            adapter.submitList(settings.profilesList)

            val enabledProfile = settings.profilesList.first { it.selected }

            binding.nsfwBlurEnabled.isChecked = enabledProfile.blurNsfw
            binding.postPreviewsEnabled.isChecked = enabledProfile.showPreviews
        }

        binding.nsfwBlurEnabled.setOnClickListener {
            viewModel.setBlurNsfw()
        }
    }
}