package me.cniekirk.flex.data.remote.model.reddit.listings

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.cniekirk.flex.data.remote.model.reddit.base.Listing
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedContribution

@JsonClass(generateAdapter = true)
class ContributionListing(
    @Json(name = "modhash")
    override val modhash: String?,
    @Json(name = "dist")
    override val dist: Int?,
    @Json(name = "children")
    override val children: List<EnvelopedContribution>,
    @Json(name = "after")
    override val after: String?,
    @Json(name = "before")
    override val before: String?
) : Listing<EnvelopedContribution>