package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Gildings(
    @Json(name = "gid_1")
    val gid1: Int?,
    @Json(name = "gid_2")
    val gid2: Int?
)