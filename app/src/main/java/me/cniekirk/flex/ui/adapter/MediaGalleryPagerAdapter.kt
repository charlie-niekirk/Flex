package me.cniekirk.flex.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.MetaDataItem
import me.cniekirk.flex.ui.gallery.ImageItemFragment
import me.cniekirk.flex.ui.gallery.VideoItemFragment

class MediaGalleryPagerAdapter(
    private val activity: FragmentActivity,
    private val mediaMetadata: List<MetaDataItem>
) : FragmentStateAdapter(activity) {

    override fun getItemCount() = mediaMetadata.size

    override fun createFragment(position: Int): Fragment {
        val item = mediaMetadata[position]
        val url = activity.getString(
            R.string.reddit_image_url,
            item.id,
            item.m.substring(item.m.indexOf('/') + 1))
        return if (item.e.equals("Video", true)) {
            VideoItemFragment(url)
        } else {
            ImageItemFragment(url)
        }
    }

}