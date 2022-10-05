package me.cniekirk.flex.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.SettingsFragmentBinding
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.ui.adapter.SettingProfilesAdapter
import me.cniekirk.flex.ui.settings.state.SettingsSideEffect
import me.cniekirk.flex.ui.settings.state.SettingsState
import me.cniekirk.flex.ui.viewmodel.SettingsViewModel
import me.cniekirk.flex.util.viewBinding
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import org.orbitmvi.orbit.viewmodel.observe
import timber.log.Timber

/**
 * Fragment to show the possible settings the user can change within the application
 *
 * @author Charlie Niekirk
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                // Set the content
                SettingsScreen(viewModel)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.VISIBLE
        }
//        viewModel.observe(viewLifecycleOwner, ::render, ::react)
//
//        binding.addProfileButton.setOnClickListener {
//            val action = SettingsFragmentDirections.actionSettingsFragmentToAddSettingsProfileFragment()
//            binding.root.findNavController().navigate(action)
//        }
//
//        binding.nsfwBlurEnabled.setOnClickListener {
//            viewModel.setBlurNsfw()
//        }
//
//        binding.notificationsSection.setOnClickListener {
//            Timber.d("HELLO?")
//            viewModel.notificationsClicked()
//        }
    }

//    private fun render(settingsState: SettingsState) {
//        val settings = settingsState.settings
//
//        if (!settings.profilesList.isNullOrEmpty()) {
//            val adapter = SettingProfilesAdapter()
//            binding.settingProfilesList.adapter = adapter
//            adapter.submitList(settings.profilesList)
//
//            val enabledProfile = settings.profilesList.first { it.selected }
//
//            binding.nsfwBlurEnabled.isChecked = enabledProfile.blurNsfw
//            binding.postPreviewsEnabled.isChecked = enabledProfile.showPreviews
//        }
//    }
//
//    private fun react(settingsSideEffect: SettingsSideEffect) {
//        when (settingsSideEffect) {
//            SettingsSideEffect.NotificationsClicked -> {
//                Timber.d("GOT HERE")
//                findNavController().navigate(
//                    SettingsFragmentDirections.actionSettingsFragmentToNotificationSettingsFragment()
//                )
//            }
//        }
//    }
}

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val state = viewModel.collectAsState()
    
    viewModel.collectSideEffect { effect ->
        when (effect) {
            SettingsSideEffect.NotificationsClicked -> {

            }
        }
    }

    SettingsContent(state)
}

@Composable
fun SettingsContent(state: State<SettingsState>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.title_settings),
            textAlign = TextAlign.Center
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
@Preview
fun SettingsContentPreview() {
    val profile = FlexSettings.Profile.getDefaultInstance()
        .toBuilder()
        .setBlurNsfw(true)
        .setName("Default")
        .setShowPreviews(true)
        .setSelected(true)
        .setPersonalNotifications(true)
        .build()

    val settings = FlexSettings.getDefaultInstance()
        .toBuilder()
        .setDeviceToken("")
        .setDownloadLocation("")
        .addProfiles(profile)
        .build()

    val state = mutableStateOf(SettingsState(settings))
    SettingsContent(state)
}