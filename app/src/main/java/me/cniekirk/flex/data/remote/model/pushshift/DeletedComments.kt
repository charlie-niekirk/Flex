package me.cniekirk.flex.data.remote.model.pushshift


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DeletedComments(
    @Json(name = "data")
    val `data`: List<DeletedComment>? = null
)