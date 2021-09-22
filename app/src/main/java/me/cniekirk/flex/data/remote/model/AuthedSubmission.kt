package me.cniekirk.flex.data.remote.model


import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class AuthedSubmission(
    @Json(name = "all_awardings")
    val allAwardings: List<AllAwarding>?,
    @Json(name = "allow_live_comments")
    val allowLiveComments: Boolean,
    @Json(name = "approved_at_utc")
    val approvedAtUtc: Long?,
    @Json(name = "approved_by")
    val approvedBy: String?,
    val archived: Boolean,
    val author: String,
    @Json(name = "author_flair_background_color")
    val authorFlairBackgroundColor: String?,
    @Json(name = "author_flair_css_class")
    val authorFlairCssClass: String?,
    @Json(name = "author_flair_richtext")
    val authorFlairRichtext: List<AuthorFlairRichtext>?,
    @Json(name = "author_flair_template_id")
    val authorFlairTemplateId: String?,
    @Json(name = "author_flair_text")
    val authorFlairText: String?,
    @Json(name = "author_flair_text_color")
    val authorFlairTextColor: String?,
    @Json(name = "author_flair_type")
    val authorFlairType: String?,
    @Json(name = "author_fullname")
    val authorFullname: String?,
    @Json(name = "author_is_blocked")
    val authorIsBlocked: Boolean?,
    @Json(name = "author_patreon_flair")
    val authorPatreonFlair: Boolean?,
    @Json(name = "author_premium")
    val authorPremium: Boolean?,
    val awarders: @RawValue List<Any>?,
    @Json(name = "banned_at_utc")
    val bannedAtUtc: Long?,
    @Json(name = "banned_by")
    val bannedBy: String?,
    @Json(name = "can_gild")
    val canGild: Boolean,
    @Json(name = "can_mod_post")
    val canModPost: Boolean,
    val category: @RawValue Any?,
    val clicked: Boolean,
    @Json(name = "content_categories")
    val contentCategories: @RawValue Any?,
    @Json(name = "contest_mode")
    val contestMode: Boolean,
    val created: Double?,
    @Json(name = "created_utc")
    val createdUtc: Double,
    @Json(name = "discussion_type")
    val discussionType: @RawValue Any?,
    val distinguished: @RawValue Any?,
    val domain: String,
    val downs: Int,
    val edited: @RawValue Any?,
    val gilded: Int,
    val gildings: Gildings,
    @Json(name = "gallery_data")
    val galleryData: GalleryData?,
    val hidden: Boolean,
    @Json(name = "hide_score")
    val hideScore: Boolean,
    val id: String,
    @Json(name = "is_created_from_ads_ui")
    val isCreatedFromAdsUi: Boolean,
    @Json(name = "is_crosspostable")
    val isCrosspostable: Boolean,
    @Json(name = "is_meta")
    val isMeta: Boolean,
    @Json(name = "is_original_content")
    val isOriginalContent: Boolean,
    @Json(name = "is_reddit_media_domain")
    val isRedditMediaDomain: Boolean,
    @Json(name = "is_robot_indexable")
    val isRobotIndexable: Boolean,
    @Json(name = "is_self")
    val isSelf: Boolean?,
    @Json(name = "is_video")
    val isVideo: Boolean,
    val likes: Boolean?,
    @Json(name = "link_flair_background_color")
    val linkFlairBackgroundColor: String?,
    @Json(name = "link_flair_css_class")
    val linkFlairCssClass: String?,
    @Json(name = "link_flair_richtext")
    val linkFlairRichtext: List<String>?,
    @Json(name = "link_flair_text")
    val linkFlairText: String?,
    @Json(name = "link_flair_text_color")
    val linkFlairTextColor: String?,
    @Json(name = "link_flair_type")
    val linkFlairType: String?,
    val locked: Boolean,
    val media: Media?,
    @Json(name = "media_embed")
    val mediaEmbed: MediaEmbed,
    @Json(name = "media_only")
    val mediaOnly: Boolean,
    @Json(name = "mod_note")
    val modNote: String?,
    @Json(name = "mod_reason_by")
    val modReasonBy: String?,
    @Json(name = "mod_reason_title")
    val modReasonTitle: String?,
    @Json(name = "mod_reports")
    val modReports: @RawValue List<Any>?,
    val name: String,
    @Json(name = "no_follow")
    val noFollow: Boolean,
    @Json(name = "num_comments")
    val numComments: Int?,
    @Json(name = "num_crossposts")
    val numCrossposts: Int,
    @Json(name = "num_reports")
    val numReports: Int?,
    @Json(name = "over_18")
    val over18: Boolean,
    @Json(name = "parent_whitelist_status")
    val parentWhitelistStatus: String,
    val permalink: String,
    val pinned: Boolean,
    @Json(name = "post_hint")
    val postHint: String?,
    val preview: Preview?,
    val pwls: Int,
    val quarantine: Boolean,
    @Json(name = "removal_reason")
    val removalReason: String?,
    @Json(name = "removed_by")
    val removedBy: String?,
    @Json(name = "removed_by_category")
    val removedByCategory: String?,
    @Json(name = "report_reasons")
    val reportReasons: String?,
    val saved: Boolean,
    val score: Int,
    @Json(name = "secure_media")
    val secureMedia: @RawValue Any?,
    @Json(name = "secure_media_embed")
    val secureMediaEmbed: SecureMediaEmbed,
    val selftext: String?,
    @Json(name = "selftext_html")
    val selftextHtml: String?,
    @Json(name = "send_replies")
    val sendReplies: Boolean,
    val spoiler: Boolean,
    val stickied: Boolean?,
    val subreddit: String,
    @Json(name = "subreddit_id")
    val subredditId: String,
    @Json(name = "subreddit_name_prefixed")
    val subredditNamePrefixed: String,
    @Json(name = "subreddit_subscribers")
    val subredditSubscribers: Int,
    @Json(name = "subreddit_type")
    val subredditType: String,
    @Json(name = "suggested_sort")
    val suggestedSort: String?,
    val thumbnail: String,
    @Json(name = "thumbnail_height")
    val thumbnailHeight: Int?,
    @Json(name = "thumbnail_width")
    val thumbnailWidth: Int?,
    val title: String,
    @Json(name = "top_awarded_type")
    val topAwardedType: String?,
    @Json(name = "total_awards_received")
    val totalAwardsReceived: Int,
    @Json(name = "treatment_tags")
    val treatmentTags: @RawValue List<Any>?,
    val ups: Int?,
    @Json(name = "upvote_ratio")
    val upvoteRatio: Double,
    val url: String,
    @Json(name = "url_overridden_by_dest")
    val urlOverriddenByDest: String?,
    @Json(name = "user_reports")
    val userReports: @RawValue List<Any>?,
    @Json(name = "view_count")
    val viewCount: Int?,
    val visited: Boolean?,
    @Json(name = "whitelist_status")
    val whitelistStatus: String,
    val wls: Int
) : Parcelable