package me.cniekirk.flex.data.remote.model.reddit.envelopes

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.data.remote.model.reddit.base.Envelope
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind

@JsonClass(generateAdapter = true)
@Parcelize
data class EnvelopedMoreComment(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: MoreComments
) : Envelope<MoreComments>, EnvelopedCommentData(kind, data), Parcelable