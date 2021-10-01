package me.cniekirk.flex.util

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.icu.text.CompactDecimalFormat
import android.icu.util.ULocale
import android.media.session.PlaybackState
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        this.startsWith("https://redgifs.com") -> {
            block(Link.RedGifLink)
        }
        videoRegex.matches(this) -> {
            block(Link.VideoLink(this))
        }
        this.contains("reddit.com/gallery") -> {
            block(Link.RedditGallery)
        }
        this.startsWith("https://www.twitter.com") -> {
            block(Link.TwitterLink(this))
        }
        else -> {
            block(Link.ExternalLink)
        }
    }
}

fun StyledPlayerView.initialise(url: String, playWhenReady: Boolean = true): SimpleExoPlayer {
    return SimpleExoPlayer.Builder(this.context)
        .build()
        .also { exoPlayer ->
            this.player = exoPlayer
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            this.player?.playWhenReady = playWhenReady
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

fun Fragment.setCurrentScreen() {
    val bundle = Bundle().apply {
        putString(FirebaseAnalytics.Param.SCREEN_NAME, this@setCurrentScreen::class.java.simpleName)
        putString(FirebaseAnalytics.Param.SCREEN_CLASS, this@setCurrentScreen::class.java.simpleName)
    }
    FirebaseAnalytics.getInstance(requireContext()).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
}

fun Fragment.shareText(textToShare: String) {
    val share = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, textToShare)
    }, null)
    startActivity(share)
}

fun Fragment.shareMedia(mediaUri: Uri) {
    val share = Intent.createChooser(Intent().apply {
        action = Intent.ACTION_SEND
        type = requireContext().contentResolver.getType(mediaUri)
        putExtra(Intent.EXTRA_STREAM, mediaUri)
    }, null)
    startActivity(share)
}

fun Fragment.shareMedia(mediaUris: ArrayList<Uri>) {
    val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
        type = "image/*"
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaUris)
    }
    startActivity(Intent.createChooser(intent, "Share Images"))
}

suspend fun Fragment.getUriFromBitmap(bmp: Bitmap): Uri =
    withContext(Dispatchers.IO) {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        var imageUri: Uri?
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/*")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Video.Media.IS_PENDING, 1)
        }

        val contentResolver = requireContext().contentResolver

        contentResolver.also { resolver ->
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            imageUri?.let {
                resolver.openOutputStream(it)?.use { output ->
                    bmp.compress(Bitmap.CompressFormat.JPEG, 70, output)
                }
            }
        }

        contentValues.clear()
        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
        contentResolver.update(imageUri!!, contentValues, null, null)

        imageUri!!
    }

suspend fun Fragment.loadImage(url: String, onLoaded: suspend (Bitmap) -> Unit) {
    val loader = ImageLoader(requireContext())
    val request = ImageRequest.Builder(requireContext())
        .data(url)
        .allowHardware(false)
        .build()

    when (val result = loader.execute(request)) {
        is SuccessResult -> {
            val bmp = (result.drawable as BitmapDrawable).bitmap
            onLoaded(bmp)
        }
        is ErrorResult -> {
            Timber.e(result.throwable)
        }
    }
}