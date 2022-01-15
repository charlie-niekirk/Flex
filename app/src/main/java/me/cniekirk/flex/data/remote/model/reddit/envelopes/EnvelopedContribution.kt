package me.cniekirk.flex.data.remote.model.reddit.envelopes

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.reddit.base.Contribution
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind

@JsonClass(generateAdapter = true)
@Parcelize
open class EnvelopedContribution(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: Contribution
) : EnvelopedData(kind, data), Parcelable