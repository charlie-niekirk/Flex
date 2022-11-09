package me.cniekirk.flex.navigation.target

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class SubmissionListTarget : Parcelable {

    @Parcelize
    data class SubmissionsList(val subreddit: String = "apolloapp") : SubmissionListTarget()

    @Parcelize
    object SubredditSheet : SubmissionListTarget()
}
