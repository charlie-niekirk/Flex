package me.cniekirk.flex.data.remote.model
import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Listing(
    val after: String?,
    val before: String?,
    val children: List<Children>,
    val dist: Int,
    @Json(name = "geo_filter")
    val geoFilter: String?,
    val modhash: String
) : Parcelable