package me.cniekirk.flex.data.remote.model.reddit.envelopes

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.cniekirk.flex.data.remote.model.reddit.base.Envelope
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.reddit.listings.ContributionListing

@JsonClass(generateAdapter = true)
class EnvelopedContributionListing(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: ContributionListing
) : Envelope<ContributionListing>