package me.cniekirk.flex.navigation.target

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.compose.material3.Icon

sealed class CoreTarget : Parcelable {

    @Parcelize
    data class SubmissionsList(val subreddit: String = "apolloapp") : CoreTarget()

    @Parcelize
    object Search : CoreTarget()

    @Parcelize
    object Account : CoreTarget()

    @Parcelize
    object Settings : CoreTarget()
}