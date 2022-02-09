package me.cniekirk.flex.data.remote.model.reddit.envelopes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.reddit.base.Thing

@JsonClass(generateAdapter = true)
open class EnvelopedData(
    @Json(name = "kind")
    open val kind: EnvelopeKind,
    @Json(name = "data")
    open val data: Thing
)