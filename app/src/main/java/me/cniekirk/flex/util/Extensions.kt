package me.cniekirk.flex.util

import android.icu.text.CompactDecimalFormat
import android.icu.util.ULocale
import android.media.session.PlaybackState
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import me.cniekirk.flex.R
import timber.log.Timber


fun Int.condense(): String {
    val cdf = CompactDecimalFormat.getInstance(ULocale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
    return cdf.format(this)
}

fun String.selfTextPreview(): String {
    return replace("&amp;#x200B;", "").
    replace("&#x200B;", "").
    replace("#", "").
    replace("\n", " ").trim(' ')
}

private const val SECOND_MILLIS: Long = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val MONTH_MILLIS = 30 * DAY_MILLIS
private const val YEAR_MILLIS = 12 * MONTH_MILLIS

private val imageRegex = Regex("""\b(https?://\S*?\.(?:png|jpe?g|gifv?)(?:\?(?:(?:(?:[\w_-]+=[\w_-]+)(?:&[\w_-]+=[\w_-]+)*)|(?:[\w_-]+)))?)\b""")
private val videoRegex = Regex("""\b(https?://\S*?\.(?:mov|mp4|mpe?g|avi)(?:\?(?:(?:(?:[\w_-]+=[\w_-]+)(?:&[\w_-]+=[\w_-]+)*)|(?:[\w_-]+)))?)\b""")

fun Long.getElapsedTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - (this * 1000)
    return when {
        diff < MINUTE_MILLIS -> {
            "now"
        }
        diff < 2 * MINUTE_MILLIS -> {
            "1m"
        }
        diff < 50 * MINUTE_MILLIS -> {
            "${diff / MINUTE_MILLIS}m"
        }
        diff < 120 * MINUTE_MILLIS -> {
            "1h"
        }
        diff < 24 * HOUR_MILLIS -> {
            "${diff / HOUR_MILLIS}h"
        }
        diff < 48 * HOUR_MILLIS -> {
            "1d"
        }
        diff < MONTH_MILLIS -> {
            "${diff / DAY_MILLIS}d"
        }
        diff < 2 * MONTH_MILLIS -> {
            "1M"
        }
        diff < YEAR_MILLIS -> {
            "${diff / MONTH_MILLIS}M"
        }
        diff < 2 * YEAR_MILLIS -> {
            "1Y"
        }
        else -> {
            "${diff / YEAR_MILLIS}Y"
        }
    }
}

fun String.processLink(block: (Link) -> Unit) {
    when {
        imageRegex.matches(this) -> {
            block(Link.ImageLink(this))
        }
        this.startsWith("https://v.redd.it") -> {
            block(Link.RedditVideo)
        }
        videoRegex.matches(this) -> {
            block(Link.VideoLink(this))
        }
        this.startsWith("https://www.twitter.com") -> {
            block(Link.TwitterLink(this))
        }
        else -> {
            block(Link.ExternalLink)
        }
    }
}

fun StyledPlayerView.initialise(url: String): SimpleExoPlayer {
    return SimpleExoPlayer.Builder(this.context)
        .build()
        .also { exoPlayer ->
            this.player = exoPlayer
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            this.player?.playWhenReady = true
            this.player?.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_ENDED -> {
                            this@initialise.player?.seekTo(0)
                            this@initialise.player?.playWhenReady = true
                        }
                    }
                }
            })
            this.player?.prepare()
        }
}

fun Int.getDepthColour(): Int {
    return when (this) {
        1 -> R.color.green
        2 -> R.color.blue
        3 -> R.color.indigo
        4 -> R.color.purple
        5 -> R.color.pink
        6 -> R.color.red
        else -> R.color.green
    }
}