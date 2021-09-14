package me.cniekirk.flex.data.remote.model
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class ResizedIcon(
    val height: Int,
    val url: String,
    val width: Int
) : Parcelable