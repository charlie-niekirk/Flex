package me.cniekirk.flex.data.remote.model.imgur


import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class AdConfig(
    val highRiskFlags: @RawValue List<Any>?,
    val safeFlags: List<String>?,
    val showsAds: Boolean?,
    val unsafeFlags: List<String>?,
    val wallUnsafeFlags: @RawValue List<Any>?
) : Parcelable