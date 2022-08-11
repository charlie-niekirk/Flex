package me.cniekirk.flex.ui.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.NotificationSettingsFragmentBinding
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.settings.state.NotificationSettingsSideEffect
import me.cniekirk.flex.ui.settings.state.NotificationSettingsState
import me.cniekirk.flex.ui.viewmodel.NotificationSettingsViewModel
import me.cniekirk.flex.util.viewBinding
import org.orbitmvi.orbit.viewmodel.observe

@AndroidEntryPoint
class NotificationSettingsFragment : BaseFragment(R.layout.notification_settings_fragment) {

    private val binding by viewBinding(NotificationSettingsFragmentBinding::bind)
    private val viewModel by viewModels<NotificationSettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }

        viewModel.observe(viewLifecycleOwner, ::render, ::react)

        binding.personalNotificationsSwitch.setOnCheckedChangeListener { _, _ ->
            viewModel.togglePersonalNotifications()
        }
    }

    private fun render(state: NotificationSettingsState) {
        binding.personalNotificationsSwitch.isChecked = state.personalNotifications
    }

    private fun react(effect: NotificationSettingsSideEffect) {
        when (effect) {
            NotificationSettingsSideEffect.NavigateToCreateSubredditTracker -> {
                // Navigate to create subreddit tracker page
            }
        }
    }
}