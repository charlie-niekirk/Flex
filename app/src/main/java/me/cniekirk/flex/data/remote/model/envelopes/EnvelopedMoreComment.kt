package me.cniekirk.flex.data.remote.model.envelopes

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.MoreComments
import me.cniekirk.flex.data.remote.model.base.Envelope
import me.cniekirk.flex.data.remote.model.base.EnvelopeKind

@JsonClass(generateAdapter = true)
@Parcelize
data class EnvelopedMoreComment(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: MoreComments
) : Envelope<MoreComments>, EnvelopedCommentData(kind, data), Parcelable