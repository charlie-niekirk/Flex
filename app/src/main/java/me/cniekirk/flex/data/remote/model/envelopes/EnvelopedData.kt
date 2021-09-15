package me.cniekirk.flex.data.remote.model.envelopes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.cniekirk.flex.data.remote.model.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.base.Thing

@JsonClass(generateAdapter = true)
open class EnvelopedData(
    @Json(name = "kind")
    open val kind: EnvelopeKind,
    @Json(name = "data")
    open val data: Thing
)