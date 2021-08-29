package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SecureMediaEmbed(
    val content: String?,
    val height: Int?,
    @Json(name = "media_domain_url")
    val mediaDomainUrl: String?,
    val scrolling: Boolean?,
    val width: Int?
)