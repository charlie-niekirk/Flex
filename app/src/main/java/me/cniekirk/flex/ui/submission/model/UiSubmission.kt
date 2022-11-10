package me.cniekirk.flex.ui.submission.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.reddit.AuthedSubmission
import me.cniekirk.flex.data.remote.model.reddit.Resolution
import me.cniekirk.flex.ui.util.getElapsedTime
import me.cniekirk.flex.util.Link
import me.cniekirk.flex.util.processLink

@Parcelize
sealed class UiSubmission : Parcelable {
    abstract val numComments: String
    abstract val submissionId: String
    abstract val submissionName: String
    abstract val author: String

    @Parcelize
    data class SelfTextSubmission(
        val title: String,
        override val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        val url: String,
        override val numComments: String,
        val timeSincePost: String,
        override val submissionId: String,
        override val submissionName: String
    ) : UiSubmission(), Parcelable
    @Parcelize
    data class ImageSubmission(
        val title: String,
        override val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        override val numComments: String,
        val previewImage: List<Resolution>,
        val timeSincePost: String,
        override val submissionId: String,
        override val submissionName: String
    ) : UiSubmission(), Parcelable

    @Parcelize
    data class VideoSubmission(
        val title: String,
        override val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        override val numComments: String,
        val videoLink: String,
        val timeSincePost: String,
        override val submissionId: String,
        override val submissionName: String
    ) : UiSubmission(), Parcelable

    @Parcelize
    data class TwitterSubmission(
        val title: String,
        override val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        override val numComments: String,
        val tweetUrl: String,
        val tweetAuthor: String,
        val tweetAuthorHandle: String,
        val tweetAuthorVerified: Boolean,
        val tweetBody: String,
        val tweetImageUrl: String?,
        val timeSincePost: String,
        override val submissionId: String,
        override val submissionName: String
    ) : UiSubmission(), Parcelable

    @Parcelize
    data class ExternalLinkSubmission(
        val title: String,
        override val author: String,
        val selfText: String,
        val upVotes: Int,
        val upVotePercentage: Int,
        val externalLink: String,
        override val numComments: String,
        val timeSincePost: String,
        override val submissionId: String,
        override val submissionName: String
    ) : UiSubmission(), Parcelable
}

fun AuthedSubmission.toUiSubmission(): UiSubmission {
    val linkType = this.urlOverriddenByDest?.processLink() ?: Link.ExternalLink

    return if (this.isSelf == true) {
        UiSubmission.SelfTextSubmission(
            this.title,
            this.author,
            this.selftext ?: "",
            this.ups ?: 0,
            this.upvoteRatio.toInt(),
            this.url,
            this.numComments?.toString() ?: "?",
            this.createdUtc.toLong().getElapsedTime(),
            this.id,
            this.name
        )
    } else {
        when (linkType) {
            Link.ExternalLink -> {
                UiSubmission.ExternalLinkSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.urlOverriddenByDest ?: "",
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
            Link.GfycatLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.url,
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
            is Link.ImageLink -> {
                UiSubmission.ImageSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    this.preview?.images?.get(0)?.resolutions ?: listOf(),
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
            is Link.ImgurGalleryLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.url,
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
            Link.RedGifLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.url,
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
            Link.RedditGallery -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.url,
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
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
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    url,
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
            Link.StreamableLink -> {
                UiSubmission.VideoSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    url,
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
            is Link.TwitterLink -> {
                tweetDetails?.let {
                    UiSubmission.TwitterSubmission(
                        this.title,
                        this.author,
                        this.selftext ?: "",
                        this.ups ?: 0,
                        this.upvoteRatio.toInt(),
                        this.numComments?.toString() ?: "?",
                        this.url,
                        it.includes?.users?.first()?.name ?: "",
                        it.includes?.users?.first()?.username ?: "",
                        it.includes?.users?.first()?.verified == true,
                        it.data?.text ?: "",
                        it.includes?.media?.first()?.previewImageUrl,
                        this.createdUtc.toLong().getElapsedTime(),
                        this.id,
                        this.name
                    )
                } ?: run {
                    UiSubmission.SelfTextSubmission(
                        this.title,
                        this.author,
                        this.selftext ?: "",
                        this.ups ?: 0,
                        this.upvoteRatio.toInt(),
                        this.url,
                        this.numComments?.toString() ?: "?",
                        this.createdUtc.toLong().getElapsedTime(),
                        this.id,
                        this.name
                    )
                }
            }
            is Link.VideoLink -> {
                UiSubmission.VideoSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.numComments?.toString() ?: "?",
                    linkType.url,
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
            is Link.YoutubeLink -> {
                UiSubmission.SelfTextSubmission(
                    this.title,
                    this.author,
                    this.selftext ?: "",
                    this.ups ?: 0,
                    this.upvoteRatio.toInt(),
                    this.url,
                    this.numComments?.toString() ?: "?",
                    this.createdUtc.toLong().getElapsedTime(),
                    this.id,
                    this.name
                )
            }
        }
    }
}
