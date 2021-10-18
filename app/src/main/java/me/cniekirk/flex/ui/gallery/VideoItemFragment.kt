package me.cniekirk.flex.ui.gallery

import android.os.Bundle
import android.view.View
import com.google.android.exoplayer2.SimpleExoPlayer
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.VideoItemFragmentBinding
import me.cniekirk.flex.ui.BaseFragment

class VideoItemFragment(private val url: String,
                        private val simpleExoPlayer: SimpleExoPlayer) : BaseFragment(R.layout.video_item_fragment) {

    private var binding: VideoItemFragmentBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = VideoItemFragmentBinding.bind(view)

        //binding?.root?.initialise(simpleExoPlayer, url)
    }
}