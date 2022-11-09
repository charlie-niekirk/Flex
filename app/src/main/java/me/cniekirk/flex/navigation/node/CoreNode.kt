package me.cniekirk.flex.navigation.node

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumble.appyx.core.composable.Child
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.replace
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.activeIndex
import com.bumble.appyx.navmodel.spotlight.operation.activate
import com.bumble.appyx.navmodel.spotlight.transitionhandler.rememberSpotlightFader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import me.cniekirk.flex.navigation.target.CoreTarget
import me.cniekirk.flex.ui.auth.LoginPage
import me.cniekirk.flex.ui.compose.bottomsheet.BottomSheetScaffold
import me.cniekirk.flex.ui.compose.bottomsheet.BottomSheetState
import me.cniekirk.flex.ui.compose.bottomsheet.BottomSheetValue
import me.cniekirk.flex.ui.compose.bottomsheet.rememberBottomSheetScaffoldState
import me.cniekirk.flex.ui.search.SearchPage
import me.cniekirk.flex.ui.settings.SettingsPage
import me.cniekirk.flex.ui.submission.SubmissionList
import me.cniekirk.flex.ui.submission.model.UiSubmission

class CoreNode(
    buildContext: BuildContext,
    private val spotlight: Spotlight<CoreTarget> = Spotlight(
        items = listOf(
            CoreTarget.SubmissionsList(),
            CoreTarget.Search,
            CoreTarget.Account,
            CoreTarget.Settings
        ),
        savedStateMap = buildContext.savedStateMap
    ),
    private val onSubmissionClick: (UiSubmission) -> Unit,
    private val onSubmissionLongClick: (UiSubmission) -> Unit
) : ParentNode<CoreTarget>(
    navModel = spotlight,
    buildContext = buildContext
) {
    private var subreddit: String? = null
    private val bottomSheetValue = MutableStateFlow(BottomSheetValue.Collapsed)

    @OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterial3Api::class)
    override fun resolve(navTarget: CoreTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            CoreTarget.Account -> node(buildContext) {
                LoginPage {
                    // replace with the authenticated page
                }
            }
            CoreTarget.Search -> node(buildContext) {
                SearchPage { searchResult ->
                    subreddit = searchResult
                    spotlight.activate(0)
                }
            }
            CoreTarget.Settings -> node(buildContext) {
                SettingsPage()
            }
            // TODO: Make it a parent node with it's own sheet animations
            is CoreTarget.SubmissionsList -> node(buildContext) {
                val scope = rememberCoroutineScope()
                SubmissionList(
                    subreddit = subreddit ?: "ukpersonalfinance",
                    onClick = { onSubmissionClick(it) },
                    onLongClick = {
                        // I need to expand/contract the bottom sheet here...
                        scope.launch {
                            val state = when (bottomSheetValue.value) {
                                BottomSheetValue.Collapsed -> { BottomSheetValue.Expanded }
                                BottomSheetValue.Expanded -> { BottomSheetValue.Collapsed }
                            }
                            bottomSheetValue.emit(state)
                        }
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
    @Composable
    override fun View(modifier: Modifier) {

        val bottomValue = bottomSheetValue.collectAsState()
        val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = BottomSheetState(bottomValue.value)
        )
        val currentlyActive = spotlight.activeIndex().collectAsState(initial = 0)

        BottomSheetScaffold(
            scaffoldState = bottomSheetScaffoldState,
            sheetContent = {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ),
                ) {
                    Text(text = "Something crazy bro")
                }
            },
            sheetPeekHeight = 0.dp
        ) {
            Scaffold(
                bottomBar = {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.List, contentDescription = "Submission list") },
                            label = { Text("Posts") },
                            selected = currentlyActive.value == 0,
                            onClick = {
                                spotlight.activate(0)
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                            label = { Text("Search") },
                            selected = currentlyActive.value == 1,
                            onClick = {
                                spotlight.activate(1)
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Account") },
                            label = { Text("Account") },
                            selected = currentlyActive.value == 2,
                            onClick = {
                                spotlight.activate(2)
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                            label = { Text("Settings") },
                            selected = currentlyActive.value == 3,
                            onClick = {
                                spotlight.activate(3)
                            }
                        )
                    }
                }
            ) { paddingValues ->
                Children(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                    navModel = spotlight,
                    transitionHandler = rememberSpotlightFader()
                )
            }
        }
    }
}