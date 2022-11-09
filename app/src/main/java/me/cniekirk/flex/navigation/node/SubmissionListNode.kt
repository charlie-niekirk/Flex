package me.cniekirk.flex.navigation.node

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.cniekirk.flex.navigation.animation.rememberSheetAnimation
import me.cniekirk.flex.navigation.target.SubmissionListTarget
import me.cniekirk.flex.ui.submission.SubmissionList
import me.cniekirk.flex.ui.submission.model.UiSubmission

class SubmissionListNode(
    buildContext: BuildContext,
    private val backStack: BackStack<SubmissionListTarget> = BackStack(
        initialElement = SubmissionListTarget.SubmissionsList(),
        savedStateMap = buildContext.savedStateMap
    ),
    private val onSubmissionClick: (UiSubmission) -> Unit,
    private val onSubmissionLongClick: (UiSubmission) -> Unit
) : ParentNode<SubmissionListTarget>(navModel = backStack, buildContext = buildContext) {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(navTarget: SubmissionListTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            is SubmissionListTarget.SubmissionsList -> node(buildContext) {
                SubmissionList(
                    subreddit = navTarget.subreddit,
                    onClick = { onSubmissionClick(it) },
                    onLongClick = { backStack.push(SubmissionListTarget.SubredditSheet) }
                )
            }
            SubmissionListTarget.SubredditSheet -> node(buildContext) {
                SheetExample()
            }
        }
    }

    @Composable
    fun SheetExample() {
        Column(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)) {

        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Column {
            // Let's add the children to the composition
            Children(
                modifier = Modifier.fillMaxSize(),
                navModel = backStack,
                transitionHandler = rememberSheetAnimation()
            )
        }
    }
}