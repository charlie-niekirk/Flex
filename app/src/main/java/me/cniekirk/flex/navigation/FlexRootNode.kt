package me.cniekirk.flex.navigation

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.navigation.animation.rememberBackstackDefaultAnimation
import me.cniekirk.flex.navigation.node.CoreNode
import me.cniekirk.flex.ui.submission.SubmissionDetail
import me.cniekirk.flex.ui.submission.model.UiSubmission

class FlexRootNode(
    buildContext: BuildContext,
    private val backStack: BackStack<NavTarget> = BackStack(
        initialElement = NavTarget.Core,
        savedStateMap = buildContext.savedStateMap,
    )
) : ParentNode<NavTarget>(
    navModel = backStack,
    buildContext = buildContext
) {

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Core -> {
                CoreNode(
                    buildContext,
                    onSubmissionClick = { submission ->
                        backStack.push(NavTarget.SubmissionDetail(submission))
                    },
                    onSubmissionLongClick = { submission ->
                        backStack.push(NavTarget.SubmissionActions(submission))
                    }
                )
            }
            is NavTarget.SubmissionDetail -> node(buildContext) {
                SubmissionDetail(navTarget.submission)
            }
            is NavTarget.SubmissionActions -> node(buildContext) {
                // Bottom sheet
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
        LaunchedEffect(key1 = backStack.screenState) {
            backStack.screenState.collect {
                it.
            }
        }

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

    @Parcelize
    data class SubmissionActions(val submission: UiSubmission) : NavTarget()
}