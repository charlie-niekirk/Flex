package me.cniekirk.flex.data.remote.redgifs


import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserData(
    val description: String?,
    val followers: Int?,
    val following: Int?,
    val name: Any?,
    val profileImageUrl: Any?,
    val profileUrl: String?,
    val publishedGfycats: Int?,
    val subscription: Int?,
    val url: String?,
    val username: String?,
    val verified: Boolean?,
    val views: Int?
)