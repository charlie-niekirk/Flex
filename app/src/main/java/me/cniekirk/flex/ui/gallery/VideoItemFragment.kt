package me.cniekirk.flex.ui.gallery

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.SimpleExoPlayer
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.VideoItemFragmentBinding
import me.cniekirk.flex.util.initialise

class VideoItemFragment(private val url: String) : Fragment(R.layout.video_item_fragment) {

    private var binding: VideoItemFragmentBinding? = null
    private var player: SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = VideoItemFragmentBinding.bind(view)

        player = binding?.root?.initialise(url)
    }
}