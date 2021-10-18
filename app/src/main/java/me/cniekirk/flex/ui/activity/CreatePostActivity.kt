package me.cniekirk.flex.ui.activity

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.color.MaterialColors
import com.google.android.material.transition.platform.MaterialArcMotion
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.CreatePostActivtyBinding

class CreatePostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        val binding = CreatePostActivtyBinding.inflate(layoutInflater)
        binding.transitionRoot.transitionName = "material_motion_container"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())

        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(binding.transitionRoot)
            setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
            pathMotion = MaterialArcMotion()
            duration = 300L
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(binding.transitionRoot)
            setAllContainerColors(MaterialColors.getColor(binding.root, R.attr.colorSurface))
            pathMotion = MaterialArcMotion()
            duration = 250L
            interpolator = FastOutSlowInInterpolator()
            fadeMode = MaterialContainerTransform.FADE_MODE_IN
        }
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        binding.cancelButton.setOnClickListener { onBackPressed() }
        binding.closeButton.setOnClickListener { onBackPressed() }

        binding.flairContainer.clipToOutline = true
    }

}