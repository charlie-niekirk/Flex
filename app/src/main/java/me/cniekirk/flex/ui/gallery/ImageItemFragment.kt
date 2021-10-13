package me.cniekirk.flex.ui.gallery

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.ImageItemFragmentBinding
import me.cniekirk.flex.ui.BaseFragment
import me.cniekirk.flex.util.viewBinding

class ImageItemFragment(private val url: String) : BaseFragment(R.layout.image_item_fragment) {

    private val binding by viewBinding(ImageItemFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(binding.image)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(binding.image)
    }

}