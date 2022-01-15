package me.cniekirk.flex.data.remote.model.reddit

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
@JsonClass(generateAdapter = true)
data class JsonResponse(
    val errors: @RawValue List<Any?>?,
    val data: ListedData
) : Parcelable
