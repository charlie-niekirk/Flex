package me.cniekirk.flex.navigation.node

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.backstack.BackStack
import com.bumble.appyx.navmodel.backstack.operation.replace
import com.bumble.appyx.navmodel.backstack.transitionhandler.rememberBackstackFader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import me.cniekirk.flex.navigation.target.CoreTarget
import me.cniekirk.flex.ui.auth.LoginPage
import me.cniekirk.flex.ui.search.SearchPage
import me.cniekirk.flex.ui.settings.SettingsPage
import me.cniekirk.flex.ui.submission.SubmissionList
import me.cniekirk.flex.ui.submission.model.UiSubmission

class CoreNode(
    buildContext: BuildContext,
    private val backStack: BackStack<CoreTarget> = BackStack(
        initialElement = CoreTarget.SubmissionsList(),
        savedStateMap = buildContext.savedStateMap,
    ),
    private val onSubmissionClick: (UiSubmission) -> Unit,
    private val onSubmissionLongClick: (UiSubmission) -> Unit
) : ParentNode<CoreTarget>(
    navModel = backStack,
    buildContext = buildContext
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun resolve(navTarget: CoreTarget, buildContext: BuildContext): Node {
        return when (navTarget) {
            CoreTarget.Account -> node(buildContext) {
                LoginPage {
                    // replace with the authenticated page
                }
            }
            CoreTarget.Search -> node(buildContext) {
                SearchPage {
                    backStack.replace(CoreTarget.SubmissionsList(it))
                }
            }
            CoreTarget.Settings -> node(buildContext) {
                SettingsPage()
            }
            // TODO: Make it a parent node with it's own sheet animations
            is CoreTarget.SubmissionsList -> node(buildContext) {
                SubmissionList(
                    subreddit = navTarget.subreddit,
                    onClick = { onSubmissionClick(it) },
                    onLongClick = { onSubmissionLongClick(it) }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun View(modifier: Modifier) {
        var selectedItem by remember { mutableStateOf(0) }

        val item = backStack.screenState.collectAsStateWithLifecycle()
        selectedItem = when (item.value.onScreen.first().key.navTarget) {
            CoreTarget.Account -> { 2 }
            CoreTarget.Search -> { 1 }
            CoreTarget.Settings -> { 3 }
            is CoreTarget.SubmissionsList -> { 0 }
        }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.List, contentDescription = "Submission list") },
                        label = { Text("Posts") },
                        selected = selectedItem == 0,
                        onClick = {
                            selectedItem = 0
                            backStack.replace(CoreTarget.SubmissionsList())
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        selected = selectedItem == 1,
                        onClick = {
                            selectedItem = 1
                            backStack.replace(CoreTarget.Search)
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Account") },
                        label = { Text("Account") },
                        selected = selectedItem == 2,
                        onClick = {
                            selectedItem = 2
                            backStack.replace(CoreTarget.Account)
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                        label = { Text("Settings") },
                        selected = selectedItem == 3,
                        onClick = {
                            selectedItem = 3
                            backStack.replace(CoreTarget.Settings)
                        }
                    )
                }
            }
        ) { paddingValues ->
            Children(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                navModel = backStack,
                transitionHandler = rememberBackstackFader()
            )
        }
    }
}