package me.cniekirk.flex.navigation.children

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import me.cniekirk.flex.ui.submission.SubmissionDetail
import me.cniekirk.flex.ui.submission.model.UiSubmission

class SubmissionDetailNode(
    buildContext: BuildContext,
    private val uiSubmission: UiSubmission
) : Node(buildContext) {

    @Composable
    override fun View(modifier: Modifier) {
        SubmissionDetail(submission = uiSubmission)
    }
}