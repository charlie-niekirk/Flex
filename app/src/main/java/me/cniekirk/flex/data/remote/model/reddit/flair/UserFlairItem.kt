package me.cniekirk.flex.data.remote.model.reddit.flair


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.cniekirk.flex.data.remote.model.reddit.AuthorFlairRichtext

@JsonClass(generateAdapter = true)
data class UserFlairItem(
    @Json(name = "allowable_content")
    val allowableContent: String?,
    @Json(name = "background_color")
    val backgroundColor: String?,
    @Json(name = "css_class")
    val cssClass: String?,
    val id: String?,
    @Json(name = "max_emojis")
    val maxEmojis: Int?,
    @Json(name = "mod_only")
    val modOnly: Boolean?,
    @Json(name = "override_css")
    val overrideCss: Boolean?,
    val richtext: List<AuthorFlairRichtext>?,
    val text: String?,
    @Json(name = "text_color")
    val textColor: String?,
    @Json(name = "text_editable")
    val textEditable: Boolean?,
    val type: String?
)