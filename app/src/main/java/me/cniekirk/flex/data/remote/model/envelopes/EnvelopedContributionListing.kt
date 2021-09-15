package me.cniekirk.flex.data.remote.model.envelopes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.cniekirk.flex.data.remote.model.base.Envelope
import me.cniekirk.flex.data.remote.model.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.listings.ContributionListing

@JsonClass(generateAdapter = true)
class EnvelopedContributionListing(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: ContributionListing
) : Envelope<ContributionListing>