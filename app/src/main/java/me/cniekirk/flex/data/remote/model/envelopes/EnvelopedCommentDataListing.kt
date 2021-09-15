package me.cniekirk.flex.data.remote.model.envelopes

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.base.Envelope
import me.cniekirk.flex.data.remote.model.base.EnvelopeKind
import me.cniekirk.flex.data.remote.model.listings.CommentDataListing

@JsonClass(generateAdapter = true)
@Parcelize
class EnvelopedCommentDataListing(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: CommentDataListing
) : Envelope<CommentDataListing>, Parcelable