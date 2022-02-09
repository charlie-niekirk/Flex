package me.cniekirk.flex.data.remote.model.reddit
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Gildings(
    @Json(name = "gid_1")
    val gid1: Int?,
    @Json(name = "gid_2")
    val gid2: Int?
) : Parcelable