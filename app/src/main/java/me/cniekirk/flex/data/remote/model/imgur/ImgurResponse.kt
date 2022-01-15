package me.cniekirk.flex.data.remote.model.imgur


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgurResponse<T : Any>(
    val data: T?,
    val status: Int?,
    val success: Boolean?
)