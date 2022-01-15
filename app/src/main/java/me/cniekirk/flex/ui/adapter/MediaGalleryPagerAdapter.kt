package me.cniekirk.flex.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.imgur.ImageData
import me.cniekirk.flex.data.remote.model.imgur.ImgurResponse
import me.cniekirk.flex.data.remote.model.reddit.MetaDataItem
import me.cniekirk.flex.ui.gallery.ImageItemFragment

class MediaGalleryPagerAdapter(
    private val activity: FragmentActivity,
    private val mediaMetadata: List<MetaDataItem>? = null,
    private val imgurImages: List<String>? = null
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = mediaMetadata?.size ?: imgurImages?.size ?: 0

    override fun createFragment(position: Int): Fragment {
        mediaMetadata?.let {
            val item = mediaMetadata[position]
            val url = activity.getString(
                R.string.reddit_image_url,
                item.id,
                item.m?.let { it.substring(it.indexOf('/') + 1) } ?: run { "jpg" })
            return if (item.e.equals("Video", true)) {
                ImageItemFragment(url)
//            VideoItemFragment(url, SimpleExoPlayer)
            } else {
                ImageItemFragment(url)
            }
        }
        imgurImages?.let {
            return ImageItemFragment(imgurImages[position])
        }
        return ImageItemFragment("")
    }

}