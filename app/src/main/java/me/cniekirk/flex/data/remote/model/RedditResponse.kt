package me.cniekirk.flex.data.remote.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class RedditResponse<T>(val kind: String, val data: @RawValue T) : Parcelable
