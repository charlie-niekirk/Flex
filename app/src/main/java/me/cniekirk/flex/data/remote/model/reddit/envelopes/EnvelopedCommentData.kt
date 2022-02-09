package me.cniekirk.flex.data.remote.model.reddit.envelopes

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.reddit.CommentData
import me.cniekirk.flex.data.remote.model.reddit.base.EnvelopeKind

@JsonClass(generateAdapter = true)
@Parcelize
open class EnvelopedCommentData(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: CommentData
) : EnvelopedContribution(kind, data), Parcelable