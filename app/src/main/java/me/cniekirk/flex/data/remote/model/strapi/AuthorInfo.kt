package me.cniekirk.flex.data.remote.model.strapi


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AuthorInfo(
    @Json(name = "id")
    val id: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "__typename")
    val typename: String?
)