package me.cniekirk.flex.data.remote.model.envelopes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.Submission
import me.cniekirk.flex.data.remote.model.base.Envelope
import me.cniekirk.flex.data.remote.model.base.EnvelopeKind

@JsonClass(generateAdapter = true)
@Parcelize
data class EnvelopedSubmission(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: Submission
) : Envelope<Submission>, EnvelopedContribution(kind, data)