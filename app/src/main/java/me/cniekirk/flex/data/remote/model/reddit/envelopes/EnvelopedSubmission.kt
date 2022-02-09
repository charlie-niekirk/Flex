package me.cniekirk.flex.data.remote.model.reddit.envelopes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.reddit.Submission
import me.cniekirk.flex.data.remote.model.reddit.base.Envelope
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind

@JsonClass(generateAdapter = true)
@Parcelize
data class EnvelopedSubmission(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: Submission
) : Envelope<Submission>, EnvelopedContribution(kind, data)