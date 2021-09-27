package me.cniekirk.flex.ui.gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import coil.load
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.ImageItemFragmentBinding

class ImageItemFragment(private val url: String) : Fragment(R.layout.image_item_fragment) {

    private var binding: ImageItemFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ImageItemFragmentBinding.bind(view)

        binding?.image?.load(url) {
            crossfade(true)
        }
    }

}