package me.cniekirk.flex.data.remote.model
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Preview(
    val enabled: Boolean,
    val images: List<Image>
) : Parcelable