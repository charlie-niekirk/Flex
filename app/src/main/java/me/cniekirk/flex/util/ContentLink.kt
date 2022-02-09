package me.cniekirk.flex.util

sealed class ContentLink(open val url: String) {
    data class ImgurGalleryLink(override val url: String) : ContentLink(url)
    data class ImgurImageLink(override val url: String) : ContentLink(url)
    data class ImageLink(override val url: String) : ContentLink(url)
    data class VideoLink(override val url: String) : ContentLink(url)
    data class YoutubeLink(override val url: String) : ContentLink(url)
    data class WikipediaLink(override val url: String, var title: String? = "", var summary: String? = "") : ContentLink(url)
    data class WebLink(override val url: String) : ContentLink(url)
}