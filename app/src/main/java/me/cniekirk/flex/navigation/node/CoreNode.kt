package me.cniekirk.flex.navigation.node

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumble.appyx.core.composable.Children
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.navmodel.spotlight.Spotlight
import com.bumble.appyx.navmodel.spotlight.activeIndex
import com.bumble.appyx.navmodel.spotlight.operation.activate
import com.bumble.appyx.navmodel.spotlight.transitionhandler.rememberSpotlightFader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.navigation.target.CoreTarget
import me.cniekirk.flex.ui.auth.LoginPage
import me.cniekirk.flex.ui.compose.bottomsheet.ModalBottomSheetLayout
import me.cniekirk.flex.ui.compose.bottomsheet.ModalBottomSheetState
import me.cniekirk.flex.ui.compose.bottomsheet.ModalBottomSheetValue
import me.cniekirk.flex.ui.compose.bottomsheet.rememberModalBottomSheetState
import me.cniekirk.flex.ui.search.SearchPage
import me.cniekirk.flex.ui.settings.SettingsPage
import me.cniekirk.flex.ui.submission.SubmissionList
import me.cniekirk.flex.ui.submission.model.UiSubmission
import me.cniekirk.flex.ui.viewmodel.SettingsViewModel

sealed class BottomSheetItem {
    abstract val actionName: String
    abstract val actionIcon: ImageVector

    data class Save(
        override val actionName: String = "Save",
        override val actionIcon: ImageVector = Icons.Default.Save
    ) : BottomSheetItem()

    data class Share(
        override val actionName: String = "Share",
        override val actionIcon: ImageVector = Icons.Default.Share
    ) : BottomSheetItem()

    data class Upvote(
        override val actionName: String = "Upvote",
        override val actionIcon: ImageVector = Icons.Default.ArrowUpward
    ) : BottomSheetItem()

    data class Downvote(
        override val actionName: String = "Downvote",
        override val actionIcon: ImageVector = Icons.Default.ArrowDownward
    ) : BottomSheetItem()
}

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
    private val bottomSheetValue: MutableSharedFlow<UiSubmission?> = MutableSharedFlow()

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
                            bottomSheetValue.emit(it)
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun BottomSheet(
        modalBottomSheetState: ModalBottomSheetState,
        submission: UiSubmission?,
        items: List<BottomSheetItem>,
        onSave: (UiSubmission) -> Unit,
        onShare: (UiSubmission) -> Unit,
        onUpvote: (UiSubmission) -> Unit,
        onDownvote: (UiSubmission) -> Unit,
        content: @Composable () -> Unit
    ) {
        ModalBottomSheetLayout(
            sheetContent = {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Divider(
                        modifier = Modifier
                            .width(32.dp)
                            .padding(top = 16.dp)
                            .background(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            ),
                        thickness = 4.dp
                    )

                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                            .align(Alignment.Start),
                        text = stringResource(id = R.string.options_sub_title),
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
                        items.forEach {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            when (it) {
                                                is BottomSheetItem.Save -> {
                                                    submission?.let(onSave)
                                                }
                                                is BottomSheetItem.Share -> {
                                                    submission?.let(onShare)
                                                }
                                                is BottomSheetItem.Upvote -> {
                                                    submission?.let(onUpvote)
                                                }
                                                is BottomSheetItem.Downvote -> {
                                                    submission?.let(onDownvote)
                                                }
                                            }
                                        }
                                ) {
                                    Divider()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = it.actionName,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Icon(imageVector = it.actionIcon, contentDescription = it.actionIcon.name)
                                    }
                                }
                            }
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { }
                            ) {
                                Divider()
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = submission?.author ?: "?",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(
                                        imageVector = Icons.Default.AccountCircle,
                                        contentDescription = Icons.Default.AccountCircle.name
                                    )
                                }
                            }
                        }
                    }
                }
            },
            sheetState = modalBottomSheetState
        ) {
            content()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation")
    @Composable
    override fun View(modifier: Modifier) {

        val modalBottomSheetScaffoldState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden
        )
        val currentlyActive = spotlight.activeIndex().collectAsState(initial = 0)

        val state = bottomSheetValue.collectAsState(null)

        LaunchedEffect(bottomSheetValue) {
            bottomSheetValue.collect {
                modalBottomSheetScaffoldState.show()
            }
        }
        
        BottomSheet(
            modalBottomSheetState = modalBottomSheetScaffoldState,
            submission = state.value,
            onSave = { /* TODO: Tell the parent node */ },
            onShare = { /* TODO: Tell the parent node */ },
            onUpvote = { /* TODO: Tell the parent node */ },
            onDownvote = { /* TODO: Tell the parent node */ },
            items = listOf(
                BottomSheetItem.Save(),
                BottomSheetItem.Share(),
                BottomSheetItem.Upvote(),
                BottomSheetItem.Downvote()
            )
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

//        BottomSheetScaffold(
//            scaffoldState = bottomSheetScaffoldState,
//            sheetContent = {
//
//            },
//            sheetPeekHeight = 0.dp
//        ) {
//
//        }
    }
}