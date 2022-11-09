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
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.push
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.parcelize.Parcelize
import me.cniekirk.flex.navigation.animation.rememberBackstackDefaultAnimation
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
    )
) : ParentNode<NavTarget>(
    navModel = backStack,
    buildContext = buildContext
) {

    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            NavTarget.Core -> {
//                val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
//                    bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
//                )
//                val scope = rememberCoroutineScope()
//
//                BottomSheetScaffold(
//                    scaffoldState = bottomSheetScaffoldState,
//                    sheetContent = {
//                        Box(
//                            Modifier
//                                .fillMaxWidth()
//                                .height(200.dp)
//                        ) {
//                            Text(text = "Hello from sheet")
//                        }
//                    }, sheetPeekHeight = 0.dp
//                ) {
                    CoreNode(
                        buildContext,
                        onSubmissionClick = { submission ->
                            backStack.push(NavTarget.SubmissionDetail(submission))
                        },
                        onSubmissionLongClick = {

                        }
                    )
//                }
            }
            is NavTarget.SubmissionDetail -> node(buildContext) {
                SubmissionDetail(navTarget.submission)
            }
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