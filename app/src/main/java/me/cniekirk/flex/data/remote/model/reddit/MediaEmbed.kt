package me.cniekirk.flex.data.remote.model.reddit
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class MediaEmbed(
    val content: String?,
    val height: Int?,
    val scrolling: Boolean?,
    val width: Int?
) : Parcelable