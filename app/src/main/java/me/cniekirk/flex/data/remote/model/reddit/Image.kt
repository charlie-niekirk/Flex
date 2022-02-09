package me.cniekirk.flex.data.remote.model.reddit
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Image(
    val id: String,
    val resolutions: List<Resolution>,
    val source: Source,
    val variants: Variants
) : Parcelable