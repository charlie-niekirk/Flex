package me.cniekirk.flex.data.remote.model.envelopes

import android.os.Parcelable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.CommentData
import me.cniekirk.flex.data.remote.model.base.EnvelopeKind

@JsonClass(generateAdapter = true)
@Parcelize
open class EnvelopedCommentData(
    @Json(name = "kind")
    override val kind: EnvelopeKind,
    @Json(name = "data")
    override val data: CommentData
) : EnvelopedContribution(kind, data), Parcelable