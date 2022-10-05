package me.cniekirk.flex.ui.settings

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.AddSettingProfileFragmentBinding
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.util.viewBinding

/**
 * Fragment that allows the user to add a new settings profile e.g. "Work", "Commute", "Home" etc.
 *
 * @author Charlie Niekirk
 */
@AndroidEntryPoint
class AddSettingsProfileFragment : BaseFragment(R.layout.add_setting_profile_fragment) {

    private val binding by viewBinding(AddSettingProfileFragmentBinding::bind)
    private var circle: Circle? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        val bottomBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }

        binding.geofenceEnabled.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.geofenceMap.visibility = View.VISIBLE
                binding.geofenceRadiusSlider.visibility = View.VISIBLE
                binding.geofenceMap.onCreate(Bundle())
                binding.geofenceMap.onStart()
                binding.geofenceMap.onResume()
                binding.geofenceMap.getMapAsync { googleMap ->
                    googleMap.setOnMapLongClickListener {
                        addCircle(googleMap, it)
                    }
                }
            } else {
                binding.geofenceMap.visibility = View.GONE
                binding.geofenceRadiusSlider.visibility = View.GONE
            }
        }

        binding.geofenceRadiusSlider.setLabelFormatter { value: Float ->
            if (value > 1000.0) {
                "${(value / 1000.0).toInt()} KM"
            } else {
                "${value.toInt()} M"
            }
        }

        binding.geofenceRadiusSlider.addOnChangeListener { _, value, _ ->
            circle?.radius = value.toDouble()
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding.geofenceMap.isVisible) {
            binding.geofenceMap.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (binding.geofenceMap.isVisible) {
            binding.geofenceMap.onStop()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (binding.geofenceMap.isVisible) {
            binding.geofenceMap.onLowMemory()
        }
    }

    private fun addCircle(googleMap: GoogleMap, location: LatLng) {
        circle?.remove()
        circle = googleMap.addCircle(
            CircleOptions()
                .center(location)
                .radius(500.0)
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.circle_stroke))
                .fillColor(ContextCompat.getColor(requireContext(), R.color.circle_fill))
        )
    }
}