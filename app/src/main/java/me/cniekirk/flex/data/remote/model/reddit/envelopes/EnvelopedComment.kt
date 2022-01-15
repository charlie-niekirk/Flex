package me.cniekirk.flex.data.remote.model.reddit.envelopes

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.base.Envelope
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind

@JsonClass(generateAdapter = true)
@Parcelize
class EnvelopedComment(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: Comment
) : Envelope<Comment>, EnvelopedCommentData(kind, data), Parcelable