package me.cniekirk.flex.data.remote.model
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetaDataItem(
    val e: String,
    val id: String,
    val m: String,
    val p: List<P>,
    val s: S,
    val status: String
)