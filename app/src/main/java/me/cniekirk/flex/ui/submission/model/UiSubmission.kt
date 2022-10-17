package me.cniekirk.flex.ui.submission.model

import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Resolution
import me.cniekirk.flex.ui.util.getElapsedTime
import me.cniekirk.flex.util.Link
import me.cniekirk.flex.util.processLink

sealed class UiSubmission {
    data class SelfTextSubmission(
        val title: String,
        val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        val numComments: String,
        val timeSincePost: String,
        val submissionId: String
    ) : UiSubmission()
    data class ImageSubmission(
        val title: String,
        val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        val numComments: String,
        val previewImage: List<Resolution>,
        val timeSincePost: String,
        val submissionId: String
    ) : UiSubmission()

    data class VideoSubmission(
        val title: String,
        val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        val numComments: String,
        val videoLink: String,
        val timeSincePost: String,
        val submissionId: String
    ) : UiSubmission()

    data class TwitterSubmission(
        val title: String,
        val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        val numComments: String,
        val tweetAuthor: String,
        val tweetAuthorHandle: String,
        val tweetAuthorVerified: Boolean,
        val tweetBody: String,
        val tweetImageUrl: String?,
        val timeSincePost: String,
        val submissionId: String
    ) : UiSubmission()
}



fun AuthedSubmission.toUiSubmission(): UiSubmission {
    val linkType = this.urlOverriddenByDest?.processLink() ?: Link.ExternalLink

    return if (this.isSelf == true) {
        UiSubmission.SelfTextSubmission(
            this.title,
            this.authorFullname ?: "unknown",
            this.selftext ?: "",
            this.ups ?: 0,
            this.upvoteRatio.toInt(),
            this.numComments?.toString() ?: "?",
            this.createdUtc.toLong().getElapsedTime(),
            this.id
        )
    } else {
        when (linkType) {
            Link.ExternalLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            Link.GfycatLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            is Link.ImageLink -> {
                UiSubmission.ImageSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    this.preview?.images?.get(0)?.resolutions ?: listOf(),
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            is Link.ImgurGalleryLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            Link.RedGifLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            Link.RedditGallery -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            Link.RedditVideo -> {
                val url = if (this.crosspostParentList.isNullOrEmpty()) {
                    this.media?.redditVideo?.dashUrl ?: this.media?.redditVideo?.fallbackUrl!!
                } else {
                    this.crosspostParentList[0].media?.redditVideo?.dashUrl
                        ?: this.crosspostParentList[0].media?.redditVideo?.fallbackUrl!!
                }

                UiSubmission.VideoSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    url,
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            Link.StreamableLink -> {
                UiSubmission.VideoSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    url,
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            is Link.TwitterLink -> {
                tweetDetails?.let {
                    UiSubmission.TwitterSubmission(
                        this.title,
                        this.authorFullname ?: "unknown",
                        this.selftext ?: "",
                        this.ups ?: 0,
                        this.upvoteRatio.toInt(),
                        this.numComments?.toString() ?: "?",
                        it.includes?.users?.first()?.name ?: "",
                        it.includes?.users?.first()?.username ?: "",
                        it.includes?.users?.first()?.verified == true,
                        it.data?.text ?: "",
                        it.includes?.media?.first()?.previewImageUrl,
                        this.createdUtc.toLong().getElapsedTime(),
                        this.id
                    )
                } ?: run {
                    UiSubmission.SelfTextSubmission(
                        this.title,
                        this.authorFullname ?: "unknown",
                        this.selftext ?: "",
                        this.ups ?: 0,
                        this.upvoteRatio.toInt(),
                        this.numComments?.toString() ?: "?",
                        this.createdUtc.toLong().getElapsedTime(),
                        this.id
                    )
                }
            }
            is Link.VideoLink -> {
                UiSubmission.VideoSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    linkType.url,
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
            is Link.YoutubeLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.authorFullname ?: "unknown",
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id
                )
            }
        }
    }
}
