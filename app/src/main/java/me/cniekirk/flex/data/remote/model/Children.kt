package me.cniekirk.flex.data.remote.model
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class Children<T>(
    val data: @RawValue T,
    val kind: String
) : Parcelable