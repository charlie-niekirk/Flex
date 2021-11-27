package me.cniekirk.flex.data.remote.imgur


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImgurResponse(
    val data: Data?,
    val status: Int?,
    val success: Boolean?
)