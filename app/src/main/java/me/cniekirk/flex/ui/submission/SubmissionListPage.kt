package me.cniekirk.flex.ui.submission

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.FlexSettings
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.*
import me.cniekirk.flex.ui.compose.VideoPlayer
import me.cniekirk.flex.ui.compose.rememberLazyListState
import me.cniekirk.flex.ui.compose.styles.FlexTheme
import me.cniekirk.flex.ui.submission.model.UiSubmission
import me.cniekirk.flex.ui.submission.model.toUiSubmission
import me.cniekirk.flex.ui.submission.state.SubmissionListSideEffect
import me.cniekirk.flex.ui.submission.state.SubmissionListState
import me.cniekirk.flex.ui.submission.state.VoteState
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SubmissionList(
    viewModel: SubmissionListViewModel = viewModel(),
    subreddit: String = "",
    onClick: (UiSubmission) -> Unit,
    onLongClick: (UiSubmission) -> Unit
) {
    if (subreddit.isNotEmpty()) {
        viewModel.updateSubreddit(subreddit)
    }

    val context = LocalContext.current
    val state = viewModel.collectAsState()
    viewModel.collectSideEffect { effect ->
        when (effect) {
            SubmissionListSideEffect.SubmissionReminderSet -> {
                Toast.makeText(context, R.string.reminder_set_message, Toast.LENGTH_SHORT).show()
            }
            is SubmissionListSideEffect.Error -> {
                Toast.makeText(context, effect.errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    SubmissionListContent(state, onClick, onLongClick)
}

@Composable
fun SubmissionListContent(
    state: State<SubmissionListState>,
    onClick: (UiSubmission) -> Unit,
    onLongClick: (UiSubmission) -> Unit
) {
    val pagingItems = state.value.submissions.collectAsLazyPagingItems()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("subreddit"),
            text = state.value.subreddit,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        LazyColumn(
            modifier = Modifier
                .padding(top = 16.dp)
                .testTag("submission_list"),
            state = pagingItems.rememberLazyListState()
        ) {
            itemsIndexed(pagingItems) { _, item ->
                when (item) {
                    is UiSubmission.ImageSubmission -> {
                        ImageItem(modifier = Modifier.padding(top = 8.dp), item) {
                            onClick(item)
                        }
                    }
                    is UiSubmission.SelfTextSubmission -> {
                        SelfTextItem(modifier = Modifier.padding(top = 8.dp), item,
                            onClick = { onClick(item) },
                            onLongClick = { onLongClick(item) }
                        )
                    }
                    is UiSubmission.VideoSubmission -> {
                        VideoItem(modifier = Modifier.padding(top = 8.dp), item = item,
                            onClick = { onClick(item) },
                            onLongClick = { onLongClick(item) }
                        )
                    }
                    is UiSubmission.TwitterSubmission -> {
                        TweetItem(modifier = Modifier.padding(top = 8.dp), item = item,
                            onClick = { onClick(item) },
                            onLongClick = { onLongClick(item) }
                        )
                    }
                    is UiSubmission.ExternalLinkSubmission -> {

                    }
                    null -> {}
                }
            }

            if (pagingItems.loadState.refresh == LoadState.Loading) {
                item {
                    Column(
                        modifier = Modifier.fillParentMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (pagingItems.loadState.append == LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .padding(top = 8.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SubmissionItem(
    modifier: Modifier = Modifier,
    title: String,
    author: String,
    upvote: Int,
    numComments: String,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.combinedClickable(
            onClick = { onClick() },
            onLongClick = { onLongClick() }
        )
    ) {
        Column(modifier = modifier.fillMaxWidth()) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
                content()
                ItemFooter(
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                    author = author,
                    upvote = "$upvote",
                    commentCount = numComments
                )
            }
            Divider()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfTextItem(
    modifier: Modifier = Modifier,
    item: UiSubmission.SelfTextSubmission,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    SubmissionItem(
        modifier = modifier,
        title = item.title,
        author = item.author,
        upvote = item.upVotes,
        numComments = item.numComments,
        onClick = { onClick() },
        onLongClick = { onLongClick() }
    ) {
        Text(
            modifier = modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            text = item.selfText,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TweetItem(
    modifier: Modifier = Modifier,
    item: UiSubmission.TwitterSubmission,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    SubmissionItem(
        modifier = modifier,
        title = item.title,
        author = item.author,
        upvote = item.upVotes,
        numComments = item.numComments,
        onClick = { onClick() },
        onLongClick = { onLongClick() }
    ) {
        item.tweetImageUrl?.let {
            Column(modifier = Modifier.padding(all = 8.dp)) {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                    model = it,
                    contentScale = ContentScale.FillWidth,
                    contentDescription = stringResource(id = R.string.tweet_image)
                )
                Card(
                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.padding(end = 4.dp),
                                text = item.tweetAuthor,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (item.tweetAuthorVerified) {
                                Icon(
                                    modifier = Modifier
                                        .width(12.dp)
                                        .height(12.dp),
                                    painter = painterResource(id = R.drawable.twitter_verified_badge),
                                    contentDescription = stringResource(id = R.string.twitter_verified_badge)
                                )
                                Text(
                                    modifier = Modifier.padding(start = 8.dp),
                                    text = "@${item.tweetAuthorHandle}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                Text(
                                    text = "@${item.tweetAuthorHandle}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .width(12.dp),
                                painter = painterResource(id = R.drawable.twitter_bird_logo_2012),
                                contentDescription = stringResource(id = R.string.twitter_logo)
                            )
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(all = 8.dp),
                            text = item.tweetBody,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        } ?: run {
            Card(
                modifier = Modifier.padding(all = 8.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.padding(end = 4.dp),
                            text = item.tweetAuthor,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (item.tweetAuthorVerified) {
                            Icon(
                                modifier = Modifier
                                    .width(12.dp)
                                    .height(12.dp),
                                painter = painterResource(id = R.drawable.twitter_verified_badge),
                                contentDescription = stringResource(id = R.string.twitter_verified_badge)
                            )
                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = "@${item.tweetAuthorHandle}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        } else {
                            Text(
                                text = "@${item.tweetAuthorHandle}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .width(12.dp),
                            painter = painterResource(id = R.drawable.twitter_bird_logo_2012),
                            contentDescription = stringResource(id = R.string.twitter_logo)
                        )
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        text = item.tweetBody,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun VideoItem(
    modifier: Modifier = Modifier,
    item: UiSubmission.VideoSubmission,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    SubmissionItem(
        modifier = modifier,
        title = item.title,
        author = item.author,
        upvote = item.upVotes,
        numComments = item.numComments,
        onClick = { onClick() },
        onLongClick = { onLongClick() }
    ) {
        VideoPlayer(
            modifier = Modifier.padding(bottom = 8.dp),
            uri = Uri.parse(item.videoLink)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItem(modifier: Modifier = Modifier, item: UiSubmission.ImageSubmission, onClick: () -> Unit) {
    val configuration = LocalConfiguration.current
    val resolution = getSuitablePreview(configuration.screenWidthDp, item.previewImage)
    Surface(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                text = item.title,
                style = MaterialTheme.typography.bodyMedium
            )
            LocalDensity.current.run {
                val widthPx = LocalConfiguration.current.screenWidthDp.dp.toPx()
                val scale = widthPx / (resolution?.width ?: 0)
                AsyncImage(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .height((resolution?.height?.times(scale))?.toDp() ?: 0.dp)
                        .background(Color.DarkGray.copy(alpha = 0.5f)),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(resolution?.url)
                        .crossfade(true)
                        .build(),
                    contentDescription = "",
                    contentScale = ContentScale.FillHeight
                )
            }
            ItemFooter(
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                author = item.author,
                upvote = "${item.upVotes}",
                commentCount = item.numComments
            )
            Divider()
        }
    }
}

@Composable
fun ItemFooter(
    modifier: Modifier = Modifier,
    author: String,
    upvote: String,
    commentCount: String
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "by $author",
            style = MaterialTheme.typography.titleSmall
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowUpward,
                contentDescription = "Upvote icon",
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = upvote,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                Icons.Outlined.ModeComment,
                contentDescription = "Comment icon",
                modifier = Modifier
                    .width(16.dp)
                    .height(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = commentCount,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun getSuitablePreview(width: Int, previews: List<Resolution>): Resolution? {
    if (previews.isNotEmpty()) {
        var preview = previews.last()
        if (preview.width * preview.height > 700000) {
            for (i in previews.size - 1 downTo 0) {
                preview = previews[i]
                if (width >= preview.width) {
                    if (preview.width * preview.height <= 700000) {
                        return preview
                    }
                } else {
                    val height = width / preview.width * preview.height
                    if (width * height <= 700000) {
                        return preview
                    }
                }
            }
        }
        return preview
    }
    return null
}

@Preview(showBackground = true)
@Composable
fun ItemFooterPreview() {
    FlexTheme {
        ItemFooter(author = "exampleUser", upvote = "100", commentCount = "3.2K")
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun SubmissionListPreview() {
    val items = flowOf(PagingData.from(createFakeData()))
    val settings = FlexSettings.getDefaultInstance()
    val state = mutableStateOf(SubmissionListState(items, settings, "flexapp", "new"))
    FlexTheme {
        SubmissionListContent(state, onClick = {}, onLongClick = {})
    }
}

private fun createFakeData(): List<UiSubmission> {
    val post = AuthedSubmission(
        null,
        false,
        null,
        null,
        false,
        "chertycherty",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        false,
        null,
        false,
        null,
        false,
        null,
        1111111.0,
        null,
        null,
        null,
        "",
        0,
        null,
        0,
        Gildings(null, null),
        null,
        false,
        false,
        "",
        false,
        true,
        false,
        true,
        false,
        false,
        true,
        false,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        null,
        MediaEmbed(null, null, null, null),
        null,
        false,
        null,
        null,
        null,
        null,
        "",
        false,
        10,
        0,
        null,
        false,
        null,
        "",
        false,
        null,
        null,
        null,
        null,
        false,
        null,
        null,
        null,
        null,
        false,
        50,
        null,
        SecureMediaEmbed(null, null, null, null, null),
        "This is a test post with text",
        null,
        false,
        false,
        null,
        "flexforreddit",
        "",
        "r/flexforreddit",
        0,
        "",
        "",
        "",
        null,
        null,
        "Test post title",
        null,
        0,
        null,
        null,
        0.9,
        "https://www.bbc.co.uk",
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        VoteState.NoVote
    ).toUiSubmission()
    return listOf(post, post, post, post, post)
}