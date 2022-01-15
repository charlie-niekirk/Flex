package me.cniekirk.flex.data.remote.model.twitter


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    @Json(name = "created_at")
    val createdAt: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "profile_image_url")
    val profileImageUrl: String?,
    @Json(name = "username")
    val username: String?,
    @Json(name = "verified")
    val verified: Boolean?
)