package me.cniekirk.flex.ui.submission

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichText
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.TableStyle
import com.halilibo.richtext.ui.material3.Material3RichText
import com.halilibo.richtext.ui.string.RichTextStringStyle
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.Comment
import me.cniekirk.flex.data.remote.model.reddit.MoreComments
import me.cniekirk.flex.data.remote.model.reddit.Resolution
import me.cniekirk.flex.ui.compose.VideoPlayer
import me.cniekirk.flex.ui.submission.model.UiSubmission
import me.cniekirk.flex.ui.submission.state.SubmissionDetailEffect
import me.cniekirk.flex.ui.submission.state.SubmissionDetailState
import me.cniekirk.flex.ui.submission.state.UiComment
import me.cniekirk.flex.ui.submission.state.VoteState
import me.cniekirk.flex.ui.viewmodel.SubmissionDetailViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect
import timber.log.Timber


@Composable
fun SubmissionDetail(submission: UiSubmission, viewModel: SubmissionDetailViewModel = viewModel()) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()

    viewModel.getComments(submission.submissionId, "/best")

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SubmissionDetailEffect.ShowError -> {
                Toast.makeText(context, R.string.generic_network_error, Toast.LENGTH_SHORT).show()
            }
        }
    }
    SubmissionDetailContent(state, submission, viewModel::upvoteClicked, viewModel::downvoteClicked, viewModel::collapseComment)
}

@Composable
fun SubmissionDetailContent(
    state: State<SubmissionDetailState>,
    submission: UiSubmission,
    onUpvote: (String) -> Unit,
    onDownvote: (String) -> Unit,
    onCommentClick: (UiComment) -> Unit
) {
    val comments = state.value.comments
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Comments (${submission.numComments})",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            item {
                when (submission) {
                    is UiSubmission.ExternalLinkSubmission -> {

                    }
                    is UiSubmission.ImageSubmission -> {
                        ImageItem(
                            item = submission,
                            onUpvote = { onUpvote(it) },
                            onDownvote = { onDownvote(it) }
                        )
                    }
                    is UiSubmission.SelfTextSubmission -> {
                        SelfTextItem(
                            item = submission,
                            onUpvote = { onUpvote(it) },
                            onDownvote = { onDownvote(it) }
                        )
                    }
                    is UiSubmission.TwitterSubmission -> {
                        TweetItem(
                            item = submission,
                            onUpvote = { onUpvote(it) },
                            onDownvote = { onDownvote(it) }
                        )
                    }
                    is UiSubmission.VideoSubmission -> {
                        VideoItem(
                            item = submission,
                            onUpvote = { onUpvote(it) },
                            onDownvote = { onDownvote(it) }
                        )
                    }
                }
                SubmissionActions(
                    state,
                    onUpvote = { onUpvote(submission.submissionName) },
                    onDownvote = { onDownvote(submission.submissionName) }
                )
            }
            items(comments.size) { index ->
                when (val comment = comments[index]) {
                    is UiComment.Comment -> {
                        CommentItem(comment = comment) {
                            onCommentClick(it)
                        }
                    }
                    is UiComment.MoreComments -> {
//                        MoreCommentsItem(comment = comment)
                    }
                }
            }
        }
    }
}

@Composable
fun SubmissionActions(
    state: State<SubmissionDetailState>,
    onUpvote: () -> Unit,
    onDownvote: () -> Unit
) {
    Divider(modifier = Modifier.fillMaxWidth())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        UpvoteButton(voteState = state.value.voteState) { onUpvote() }
        DownvoteButton(voteState = state.value.voteState) { onDownvote() }
        Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share button"
        )
        Icon(
            imageVector = Icons.Default.Reply,
            contentDescription = "Reply button"
        )
    }
    Divider(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp))
}

@Composable
fun DownvoteButton(
    modifier: Modifier = Modifier,
    voteState: VoteState,
    onDownvote: () -> Unit
) {
    when (voteState) {
        VoteState.Downvote -> {
            Icon(
                modifier = Modifier
                    .padding(4.dp)
                    .background(Color.Red, shape = RoundedCornerShape(4.dp))
                    .clickable { onDownvote() },
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Downvote button"
            )
        }
        VoteState.Upvote, VoteState.NoVote -> {
            Icon(
                modifier = modifier
                    .padding(4.dp)
                    .clickable { onDownvote() },
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Downvote button"
            )
        }
    }
}

@Composable
fun UpvoteButton(
    modifier: Modifier = Modifier,
    voteState: VoteState,
    onUpvote: () -> Unit
) {
    when (voteState) {
        VoteState.Downvote, VoteState.NoVote -> {
            Icon(
                modifier = modifier
                    .padding(4.dp)
                    .clickable { onUpvote() },
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Downvote button"
            )
        }
        VoteState.Upvote -> {
            Icon(
                modifier = modifier
                    .padding(4.dp)
                    .background(Color.Blue, shape = RoundedCornerShape(4.dp))
                    .clickable { onUpvote() },
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Downvote button"
            )
        }
    }
}

const val DEPTH_INCREMENT = 8
val colorArray = listOf(
    Color.Blue,
    Color.Green,
    Color.Red,
    Color.Magenta,
    Color.Yellow
)

@Composable
fun CommentItem(
    comment: UiComment.Comment,
    onClick: (UiComment) -> Unit
) {
    AnimatedVisibility(
        visible = !comment.isCollapsed,
        enter = fadeIn(animationSpec = tween(150)) + expandVertically(animationSpec = tween(150)),
        exit = shrinkVertically(animationSpec = tween(150)) + fadeOut(animationSpec = tween(150))
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(comment) }
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                if (comment.depth > 0 && comment.depth < colorArray.size) {
                    Divider(
                        modifier = Modifier
                            .padding(
                                start = (DEPTH_INCREMENT * comment.depth).dp,
                                top = 2.dp,
                                bottom = 2.dp
                            )
                            .width(2.dp)
                            .fillMaxHeight(),
                        color = colorArray[comment.depth]
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(11f)
                ) {
                    Text(
                        text = comment.author,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    AnimatedVisibility(
                        visible = !comment.parentCollapsed,
                        enter = fadeIn(animationSpec = tween(150)) + expandVertically(animationSpec = tween(150)),
                        exit = shrinkVertically(animationSpec = tween(150)) + fadeOut(animationSpec = tween(150))
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                            text = comment.body
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Crossfade(targetState = comment.parentCollapsed) { parentCollapsed ->
                    if (parentCollapsed) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand icon"
                        )
                    } else {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Collapse icon"
                        )
                    }
                }
            }
            val extra = if (comment.depth > 0) 8 else 0
            Divider(modifier = Modifier.padding(start = (extra + (DEPTH_INCREMENT * comment.depth)).dp))
        }
    }
}

@Composable
fun MoreCommentsItem(comment: MoreComments) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

    }
}

@Composable
fun SubmissionItem(
    modifier: Modifier = Modifier,
    title: String,
    author: String,
    upvote: Int,
    numComments: String,
    onUpvote: (String) -> Unit,
    onDownvote: (String) -> Unit,
    content: @Composable () -> Unit,
) {
    Surface {
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
                    commentCount = numComments,
                    { onUpvote(it) },
                    { onDownvote(it) }
                )
            }
            Divider()
        }
    }
}

@Composable
fun SelfTextItem(
    modifier: Modifier = Modifier,
    item: UiSubmission.SelfTextSubmission,
    onUpvote: (String) -> Unit,
    onDownvote: (String) -> Unit
) {
    SubmissionItem(
        modifier = modifier,
        title = item.title,
        author = item.author,
        upvote = item.upVotes,
        numComments = item.numComments,
        onUpvote = { onUpvote(it) },
        onDownvote = { onDownvote(it) }
    ) {
        RichText(
            modifier = modifier.padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 8.dp),
            style = RichTextStyle.Default
        ) {
            Markdown(content = item.selfText.trimIndent())
        }
    }
}

@Composable
fun TweetItem(
    modifier: Modifier = Modifier,
    item: UiSubmission.TwitterSubmission,
    onUpvote: (String) -> Unit,
    onDownvote: (String) -> Unit
) {
    SubmissionItem(
        modifier = modifier,
        title = item.title,
        author = item.author,
        upvote = item.upVotes,
        numComments = item.numComments,
        onUpvote = { onUpvote(it) },
        onDownvote = { onDownvote(it) }
    ) {
        item.tweetImageUrl?.let {
            Column(modifier = Modifier.padding(all = 8.dp)) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
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
    onUpvote: (String) -> Unit,
    onDownvote: (String) -> Unit
) {
    SubmissionItem(
        modifier = modifier,
        title = item.title,
        author = item.author,
        upvote = item.upVotes,
        numComments = item.numComments,
        onUpvote = { onUpvote(it) },
        onDownvote = { onDownvote(it) }
    ) {
        VideoPlayer(
            modifier = Modifier.padding(bottom = 8.dp),
            uri = Uri.parse(item.videoLink)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageItem(
    modifier: Modifier = Modifier,
    item: UiSubmission.ImageSubmission,
    onUpvote: (String) -> Unit,
    onDownvote: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val resolution = getSuitablePreview(configuration.screenWidthDp, item.previewImage)
    Surface(
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
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
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
                commentCount = item.numComments,
                onUpvote = { onUpvote(it) },
                onDownvote = { onDownvote(it) }
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
    commentCount: String,
    onUpvote: (String) -> Unit,
    onDownvote: (String) -> Unit
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