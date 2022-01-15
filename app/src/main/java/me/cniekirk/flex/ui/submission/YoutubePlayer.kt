package me.cniekirk.flex.ui.submission

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.YoutubePlayerFragmentBinding
import me.cniekirk.flex.util.viewBinding


class YoutubePlayer : Fragment(R.layout.youtube_player_fragment) {

    private val args by navArgs<YoutubePlayerArgs>()
    private val binding by viewBinding(YoutubePlayerFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottom_app_bar)
        val actionButton = requireActivity().findViewById<FloatingActionButton>(R.id.floating_action_button)
        if (!actionButton.isOrWillBeHidden) {
            actionButton.hide()
            bottomAppBar.performHide()
        }

        binding.apply {
            lifecycle.addObserver(youtubePlayer)
            val listener = object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    val defaultPlayerUiController =
                        DefaultPlayerUiController(youtubePlayer, youTubePlayer)
                    youtubePlayer.setCustomPlayerUi(defaultPlayerUiController.rootView)
                    youTubePlayer.loadVideo(args.videoId, 0F)
                }
            }
            val options = IFramePlayerOptions.Builder().controls(0).build()
            youtubePlayer.initialize(listener, options)
        }
    }

}