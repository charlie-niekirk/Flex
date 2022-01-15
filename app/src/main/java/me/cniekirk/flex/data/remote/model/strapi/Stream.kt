package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Stream(
    @Json(name = "duration_limit")
    val durationLimit: Int?,
    @Json(name = "ended_at")
    val endedAt: Any?,
    @Json(name = "ended_reason")
    val endedReason: Any?,
    @Json(name = "finished_by")
    val finishedBy: Any?,
    @Json(name = "height")
    val height: Int?,
    @Json(name = "hls_exists_at")
    val hlsExistsAt: Long?,
    @Json(name = "hls_url")
    val hlsUrl: String?,
    @Json(name = "killed_at")
    val killedAt: Any?,
    @Json(name = "publish_at")
    val publishAt: Long?,
    @Json(name = "publish_done_at")
    val publishDoneAt: Any?,
    @Json(name = "purged_at")
    val purgedAt: Any?,
    @Json(name = "state")
    val state: String?,
    @Json(name = "stream_id")
    val streamId: String?,
    @Json(name = "thumbnail")
    val thumbnail: String?,
    @Json(name = "update_at")
    val updateAt: Long?,
    @Json(name = "vod_accessible")
    val vodAccessible: Boolean?,
    @Json(name = "width")
    val width: Int?
)