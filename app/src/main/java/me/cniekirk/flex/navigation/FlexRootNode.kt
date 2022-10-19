package me.cniekirk.flex.navigation

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.core.children.ChildEntry
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.pop
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.replace
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.ui.submission.SubmissionDetail
import me.cniekirk.flex.ui.submission.SubmissionList

class FlexRootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = NavTarget.SubmissionsList,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<NavTarget>(
    navModel = backStack,
    buildContext = buildContext
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.SubmissionDetail -> node(buildContext) {
                SubmissionDetail()
            }
            NavTarget.SubmissionsList -> node(buildContext) {
                SubmissionList {
//                    backStack.replace(NavTarget.SubmissionDetail)
                    backStack.push(NavTarget.SubmissionDetail)
                }
            }
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Column {
            // Let's add the children to the composition
            Children(
                modifier = Modifier.fillMaxSize(),
                navModel = backStack,
                transitionHandler = rememberBackstackDefaultAnimation()
            )
        }
    }
}

sealed class NavTarget : Parcelable {
    @Parcelize
    object SubmissionsList : NavTarget()

    @Parcelize
    object SubmissionDetail : NavTarget()
}