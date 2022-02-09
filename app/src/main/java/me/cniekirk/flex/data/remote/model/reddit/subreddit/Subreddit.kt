package me.cniekirk.flex.data.remote.model.reddit.subreddit


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class Subreddit(
    @Json(name = "accept_followers")
    val acceptFollowers: Boolean?,
    @Json(name = "accounts_active")
    val accountsActive: @RawValue Any?,
    @Json(name = "accounts_active_is_fuzzed")
    val accountsActiveIsFuzzed: Boolean?,
    @Json(name = "active_user_count")
    val activeUserCount: Int?,
    @Json(name = "advertiser_category")
    val advertiserCategory: @RawValue Any?,
    @Json(name = "all_original_content")
    val allOriginalContent: @RawValue Any?,
    @Json(name = "allow_discovery")
    val allowDiscovery: Boolean?,
    @Json(name = "allow_galleries")
    val allowGalleries: Boolean?,
    @Json(name = "allow_images")
    val allowImages: Boolean?,
    @Json(name = "allow_polls")
    val allowPolls: Boolean?,
    @Json(name = "allow_prediction_contributors")
    val allowPredictionContributors: Boolean?,
    @Json(name = "allow_predictions")
    val allowPredictions: Boolean?,
    @Json(name = "allow_predictions_tournament")
    val allowPredictionsTournament: Boolean?,
    @Json(name = "allow_videogifs")
    val allowVideogifs: Boolean?,
    @Json(name = "allow_videos")
    val allowVideos: Boolean?,
    @Json(name = "banner_background_color")
    val bannerBackgroundColor: @RawValue Any?,
    @Json(name = "banner_background_image")
    val bannerBackgroundImage: String?,
    @Json(name = "banner_img")
    val bannerImg: @RawValue Any?,
    @Json(name = "banner_size")
    val bannerSize: @RawValue Any?,
    @Json(name = "can_assign_link_flair")
    val canAssignLinkFlair: Boolean?,
    @Json(name = "can_assign_user_flair")
    val canAssignUserFlair: Boolean?,
    @Json(name = "collapse_deleted_comments")
    val collapseDeletedComments: @RawValue Any?,
    @Json(name = "comment_score_hide_mins")
    val commentScoreHideMins: @RawValue Any?,
    @Json(name = "community_icon")
    val communityIcon: String?,
    @Json(name = "community_reviewed")
    val communityReviewed: Boolean?,
    val created: Double?,
    @Json(name = "created_utc")
    val createdUtc: Double?,
    val description: String?,
    @Json(name = "description_html")
    val descriptionHtml: String?,
    @Json(name = "disable_contributor_requests")
    val disableContributorRequests: @RawValue Any?,
    @Json(name = "display_name")
    val displayName: String?,
    @Json(name = "display_name_prefixed")
    val displayNamePrefixed: String?,
    @Json(name = "emojis_custom_size")
    val emojisCustomSize: @RawValue Any?,
    @Json(name = "emojis_enabled")
    val emojisEnabled: Boolean?,
    @Json(name = "free_form_reports")
    val freeFormReports: @RawValue Any?,
    @Json(name = "has_menu_widget")
    val hasMenuWidget: Boolean?,
    @Json(name = "header_img")
    val headerImg: @RawValue Any?,
    @Json(name = "header_size")
    val headerSize: @RawValue Any?,
    @Json(name = "header_title")
    val headerTitle: @RawValue Any?,
    @Json(name = "hide_ads")
    val hideAds: @RawValue Any?,
    @Json(name = "icon_img")
    val iconImg: String?,
    @Json(name = "icon_size")
    val iconSize: @RawValue Any?,
    val id: String?,
    @Json(name = "is_crosspostable_subreddit")
    val isCrosspostableSubreddit: Boolean?,
    @Json(name = "is_enrolled_in_new_modmail")
    val isEnrolledInNewModmail: @RawValue Any?,
    @Json(name = "key_color")
    val keyColor: @RawValue Any?,
    val lang: @RawValue Any?,
    @Json(name = "link_flair_enabled")
    val linkFlairEnabled: @RawValue Any?,
    @Json(name = "link_flair_position")
    val linkFlairPosition: @RawValue Any?,
    @Json(name = "mobile_banner_image")
    val mobileBannerImage: @RawValue Any?,
    val name: String?,
    @Json(name = "notification_level")
    val notificationLevel: @RawValue Any?,
    @Json(name = "original_content_tag_enabled")
    val originalContentTagEnabled: @RawValue Any?,
    val over18: Boolean?,
    @Json(name = "prediction_leaderboard_entry_type")
    val predictionLeaderboardEntryType: String?,
    @Json(name = "primary_color")
    val primaryColor: @RawValue Any?,
    @Json(name = "public_description")
    val publicDescription: String?,
    @Json(name = "public_description_html")
    val publicDescriptionHtml: @RawValue Any?,
    @Json(name = "public_traffic")
    val publicTraffic: @RawValue Any?,
    val quarantine: @RawValue Any?,
    @Json(name = "restrict_commenting")
    val restrictCommenting: @RawValue Any?,
    @Json(name = "restrict_posting")
    val restrictPosting: @RawValue Any?,
    @Json(name = "should_archive_posts")
    val shouldArchivePosts: @RawValue Any?,
    @Json(name = "show_media")
    val showMedia: @RawValue Any?,
    @Json(name = "show_media_preview")
    val showMediaPreview: @RawValue Any?,
    @Json(name = "spoilers_enabled")
    val spoilersEnabled: @RawValue Any?,
    @Json(name = "submission_type")
    val submissionType: @RawValue Any?,
    @Json(name = "submit_link_label")
    val submitLinkLabel: @RawValue Any?,
    @Json(name = "submit_text")
    val submitText: @RawValue Any?,
    @Json(name = "submit_text_html")
    val submitTextHtml: @RawValue Any?,
    @Json(name = "submit_text_label")
    val submitTextLabel: @RawValue Any?,
    @Json(name = "subreddit_type")
    val subredditType: String?,
    val subscribers: Int?,
    @Json(name = "suggested_comment_sort")
    val suggestedCommentSort: String?,
    val title: String?,
    val url: String?,
    @Json(name = "user_can_flair_in_sr")
    val userCanFlairInSr: @RawValue Any?,
    @Json(name = "user_flair_background_color")
    val userFlairBackgroundColor: @RawValue Any?,
    @Json(name = "user_flair_css_class")
    val userFlairCssClass: @RawValue Any?,
    @Json(name = "user_flair_enabled_in_sr")
    val userFlairEnabledInSr: @RawValue Any?,
    @Json(name = "user_flair_position")
    val userFlairPosition: @RawValue Any?,
    @Json(name = "user_flair_richtext")
    val userFlairRichtext: @RawValue List<Any>?,
    @Json(name = "user_flair_template_id")
    val userFlairTemplateId: @RawValue Any?,
    @Json(name = "user_flair_text")
    val userFlairText: @RawValue Any?,
    @Json(name = "user_flair_text_color")
    val userFlairTextColor: @RawValue Any?,
    @Json(name = "user_flair_type")
    val userFlairType: String?,
    @Json(name = "user_has_favorited")
    val userHasFavorited: Boolean?,
    @Json(name = "user_is_banned")
    val userIsBanned: Boolean?,
    @Json(name = "user_is_contributor")
    val userIsContributor: Boolean?,
    @Json(name = "user_is_moderator")
    val userIsModerator: Boolean?,
    @Json(name = "user_is_muted")
    val userIsMuted: Boolean?,
    @Json(name = "user_is_subscriber")
    val userIsSubscriber: Boolean?,
    @Json(name = "user_sr_flair_enabled")
    val userSrFlairEnabled: @RawValue Any?,
    @Json(name = "user_sr_theme_enabled")
    val userSrThemeEnabled: @RawValue Any?,
    @Json(name = "whitelist_status")
    val whitelistStatus: @RawValue Any?,
    @Json(name = "wiki_enabled")
    val wikiEnabled: Boolean?,
    val wls: @RawValue Any?
): Parcelable