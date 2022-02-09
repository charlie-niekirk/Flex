package me.cniekirk.flex.data.remote.model.reddit

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.data.remote.model.reddit.envelopes.EnvelopedContribution

@Parcelize
@JsonClass(generateAdapter = true)
data class ListedData(
    val things: List<EnvelopedContribution>
) : Parcelable
