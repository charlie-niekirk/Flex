package me.cniekirk.flex.navigation

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.core.plugin.Plugin
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import com.bumble.appyx.navmodel.backstack.operation.replace
import com.bumble.appyx.navmodel.backstack.operation.singleTop
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.navigation.animation.rememberBackstackDefaultAnimation
import me.cniekirk.flex.navigation.children.SubmissionDetailNode
import me.cniekirk.flex.navigation.node.CoreNode
import me.cniekirk.flex.ui.compose.bottomsheet.BottomSheetScaffold
import me.cniekirk.flex.ui.compose.bottomsheet.BottomSheetState
import me.cniekirk.flex.ui.compose.bottomsheet.BottomSheetValue
import me.cniekirk.flex.ui.compose.bottomsheet.rememberBottomSheetScaffoldState
import me.cniekirk.flex.ui.submission.SubmissionDetail
import me.cniekirk.flex.ui.submission.model.UiSubmission

class FlexRootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = NavTarget.Core,
        savedStateMap = buildContext.savedStateMap,
    ),
    plugins: List<Plugin> = emptyList()
) : ParentNode<NavTarget>(
    navModel = backStack,
    buildContext = buildContext,
    plugins = plugins
) {

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Core -> {
                CoreNode(
                    buildContext,
                    onSubmissionClick = { submission ->
                        backStack.push(NavTarget.SubmissionDetail(submission))
                    }
                )
            }
            is NavTarget.SubmissionDetail -> SubmissionDetailNode(buildContext, navTarget.submission)
        }
    }

    /**
     * Used to directly deeplink into a Reddit post
     *
     * @param uiSubmission the submission to show the details for
     */
    suspend fun attachSubmissionDetail(uiSubmission: UiSubmission): SubmissionDetailNode {
        return attachWorkflow {
            backStack.push(NavTarget.SubmissionDetail(uiSubmission))
        }
    }

    @Composable
    override fun View(modifier: Modifier) {
        Column {
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
    object Core : NavTarget()

    @Parcelize
    data class SubmissionDetail(val submission: UiSubmission) : NavTarget()
}