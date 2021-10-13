package me.cniekirk.flex.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SettingsFragmentBinding
import me.cniekirk.flex.ui.viewmodel.SettingsViewModel
import me.cniekirk.flex.util.observe

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.settings_fragment) {

    private var binding: SettingsFragmentBinding? = null
    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SettingsFragmentBinding.bind(view)

        observe(viewModel.shouldBlurNsfw) {
            binding?.nsfwBlurEnabled?.isChecked = it ?: true
        }

        binding?.nsfwBlurEnabled?.setOnClickListener {
            viewModel.setBlurNsfw()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}