package me.cniekirk.flex.data.remote.model.reddit
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ResizedStaticIcon(
    val height: Int,
    val url: String,
    val width: Int
) : Parcelable