package me.cniekirk.flex.util.video

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import im.ene.toro.exoplayer.Config
import im.ene.toro.exoplayer.DefaultExoCreator
import im.ene.toro.exoplayer.ToroExo

class LoopExoCreator(toro: ToroExo, config: Config) : DefaultExoCreator(toro, config) {

    override fun createPlayer(): SimpleExoPlayer {
        val player = super.createPlayer()
        player.repeatMode = Player.REPEAT_MODE_ALL
        return player
    }

}