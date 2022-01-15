package me.cniekirk.flex.data.remote.model.reddit.subreddit

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class PollData(
    @Json(name = "prediction_status")
    val predictionStatus: String?,
    @Json(name = "total_stake_amount")
    val totalStakeAmount: String?,
    @Json(name = "voting_end_timestamp")
    val votingEndTimestamp: Long?,
    @Json(name = "is_prediction")
    val isPrediction: Boolean?,
    @Json(name = "total_vote_count")
    val totalVoteCount: Int?,
    val options: List<PollOption>?,
) : Parcelable
