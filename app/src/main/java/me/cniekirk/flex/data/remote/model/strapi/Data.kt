package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Data(
    @Json(name = "broadcast_time")
    val broadcastTime: Double?,
    @Json(name = "chat_disabled")
    val chatDisabled: Boolean?,
    @Json(name = "continuous_watchers")
    val continuousWatchers: Int?,
    @Json(name = "downvotes")
    val downvotes: Int?,
    @Json(name = "estimated_remaining_time")
    val estimatedRemainingTime: Double?,
    @Json(name = "global_rank")
    val globalRank: Int?,
    @Json(name = "is_first_broadcast")
    val isFirstBroadcast: Boolean?,
    @Json(name = "meter")
    val meter: Meter?,
    @Json(name = "post")
    val post: Post?,
    @Json(name = "rank")
    val rank: Int?,
    @Json(name = "rank_in_subreddit")
    val rankInSubreddit: Int?,
    @Json(name = "share_link")
    val shareLink: String?,
    @Json(name = "stream")
    val stream: Stream?,
    @Json(name = "total_continuous_watchers")
    val totalContinuousWatchers: Int?,
    @Json(name = "total_streams")
    val totalStreams: Int?,
    @Json(name = "total_streams_in_subreddit")
    val totalStreamsInSubreddit: Int?,
    @Json(name = "unique_watchers")
    val uniqueWatchers: Int?,
    @Json(name = "upvotes")
    val upvotes: Int?
)