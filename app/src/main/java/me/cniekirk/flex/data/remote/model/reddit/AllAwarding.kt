package me.cniekirk.flex.data.remote.model.reddit
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class AllAwarding(
    @Json(name = "award_sub_type")
    val awardSubType: String?,
    @Json(name = "award_type")
    val awardType: String?,
    @Json(name = "awardings_required_to_grant_benefits")
    val awardingsRequiredToGrantBenefits: @RawValue Any?,
    @Json(name = "coin_price")
    val coinPrice: Int?,
    @Json(name = "coin_reward")
    val coinReward: Int?,
    val count: Int?,
    @Json(name = "days_of_drip_extension")
    val daysOfDripExtension: Int?,
    @Json(name = "days_of_premium")
    val daysOfPremium: Int?,
    val description: String?,
    @Json(name = "end_date")
    val endDate: @RawValue Any?,
    @Json(name = "giver_coin_reward")
    val giverCoinReward: @RawValue Any?,
    @Json(name = "icon_format")
    val iconFormat: String?,
    @Json(name = "icon_height")
    val iconHeight: Int?,
    @Json(name = "icon_url")
    val iconUrl: String?,
    @Json(name = "icon_width")
    val iconWidth: Int?,
    val id: String?,
    @Json(name = "is_enabled")
    val isEnabled: Boolean?,
    @Json(name = "is_new")
    val isNew: Boolean?,
    val name: String?,
    @Json(name = "penny_donate")
    val pennyDonate: @RawValue Any?,
    @Json(name = "penny_price")
    val pennyPrice: @RawValue Any?,
    @Json(name = "resized_icons")
    val resizedIcons: List<ResizedIcon>?,
    @Json(name = "resized_static_icons")
    val resizedStaticIcons: List<ResizedStaticIcon>?,
    @Json(name = "start_date")
    val startDate: @RawValue Any?,
    @Json(name = "static_icon_height")
    val staticIconHeight: Int?,
    @Json(name = "static_icon_url")
    val staticIconUrl: String?,
    @Json(name = "static_icon_width")
    val staticIconWidth: Int?,
    @Json(name = "subreddit_coin_reward")
    val subredditCoinReward: Int?,
    @Json(name = "subreddit_id")
    val subredditId: @RawValue Any?,
    @Json(name = "tiers_by_required_awardings")
    val tiersByRequiredAwardings: @RawValue Any?
) : Parcelable