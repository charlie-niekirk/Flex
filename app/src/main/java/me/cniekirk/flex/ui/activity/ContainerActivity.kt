package me.cniekirk.flex.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.ContainerActivityBinding

@AndroidEntryPoint
class ContainerActivity : AppCompatActivity() {

    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false

        super.onCreate(savedInstanceState)
        val binding = ContainerActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //NavigationUI.setupWithNavController(binding.bottomAppBar, navController)
        setSupportActionBar(binding.bottomAppBar)
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            val options = ActivityOptions.makeSceneTransitionAnimation(
                this,
                binding.floatingActionButton,
                "material_motion_container"
            )
            startActivity(intent, options.toBundle())
        }
    }
}