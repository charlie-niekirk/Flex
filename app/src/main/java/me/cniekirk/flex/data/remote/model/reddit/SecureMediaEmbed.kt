package me.cniekirk.flex.data.remote.model.reddit
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SecureMediaEmbed(
    val content: String?,
    val height: Int?,
    @Json(name = "media_domain_url")
    val mediaDomainUrl: String?,
    val scrolling: Boolean?,
    val width: Int?
) : Parcelable