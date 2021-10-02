package me.cniekirk.flex.util

sealed class Link {
    object ExternalLink : Link()
    object RedditVideo : Link()
    data class ImageLink(val url: String) : Link()
    data class VideoLink(val url: String) : Link()
    object RedGifLink : Link()
    object RedditGallery : Link()
    data class TwitterLink(val url: String) : Link()
}
