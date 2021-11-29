package me.cniekirk.flex.data.remote.model.rules


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NextStepReason(
    val canSpecifyUsernames: Boolean?,
    val canWriteNotes: Boolean?,
    val complaintButtonText: String?,
    val complaintPageTitle: String?,
    val complaintPrompt: String?,
    val complaintUrl: String?,
    val fileComplaint: Boolean?,
    val isAbuseOfReportButton: Boolean?,
    val nextStepHeader: String?,
    val nextStepReasons: List<NextStepReasonX>?,
    val notesInputTitle: String?,
    val oneUsername: Boolean?,
    val reasonText: String?,
    val reasonTextToShow: String?,
    val requestCrisisSupport: Boolean?,
    val usernamesInputTitle: String?
)