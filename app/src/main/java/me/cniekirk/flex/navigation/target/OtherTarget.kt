package me.cniekirk.flex.navigation.target

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.ui.submission.model.UiSubmission

sealed class OtherTarget : Parcelable {
    @Parcelize
    object Core : OtherTarget()
    @Parcelize
    data class SubmissionDetail(val submission: UiSubmission) : OtherTarget()
}