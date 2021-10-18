package me.cniekirk.flex.data.remote.model
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class MetaDataItem(
    val e: String,
    val id: String,
    val m: String?,
    val p: List<P>,
    val s: S,
    val status: String
) : Parcelable