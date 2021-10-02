package me.cniekirk.flex.ui

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SettingsFragmentBinding

@AndroidEntryPoint
class SettingsFragment : BaseFragment(R.layout.settings_fragment) {

    private var binding: SettingsFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SettingsFragmentBinding.bind(view)

        binding?.apply {

        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}