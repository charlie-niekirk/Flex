package me.cniekirk.flex.data.remote.model.wikipedia


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WikiSummary(
    @Json(name = "content_urls")
    val contentUrls: ContentUrls?,
    val description: String?,
    @Json(name = "description_source")
    val descriptionSource: String?,
    val dir: String?,
    val displaytitle: String?,
    val extract: String?,
    @Json(name = "extract_html")
    val extractHtml: String?,
    val lang: String?,
    val namespace: Namespace?,
    val originalimage: Originalimage?,
    val pageid: Int?,
    val revision: String?,
    val thumbnail: Thumbnail?,
    val tid: String?,
    val timestamp: String?,
    val title: String?,
    val titles: Titles?,
    val type: String?,
    @Json(name = "wikibase_item")
    val wikibaseItem: String?
)