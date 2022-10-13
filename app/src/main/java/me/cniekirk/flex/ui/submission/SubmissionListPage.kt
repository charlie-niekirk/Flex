package me.cniekirk.flex.ui.submission

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.*
import me.cniekirk.flex.ui.compose.styles.FlexTheme
import me.cniekirk.flex.ui.submission.state.SubmissionListSideEffect
import me.cniekirk.flex.ui.submission.state.SubmissionListState
import me.cniekirk.flex.ui.submission.state.VoteState
import me.cniekirk.flex.ui.viewmodel.SubmissionListViewModel
import me.cniekirk.flex.util.Link
import me.cniekirk.flex.util.processLink
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SubmissionList(viewModel: SubmissionListViewModel = viewModel(), onClick: () -> Unit) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()
    viewModel.collectSideEffect { effect ->
        when (effect) {
            SubmissionListSideEffect.SubmissionReminderSet -> {
                Toast.makeText(context, R.string.reminder_set_message, Toast.LENGTH_SHORT).show()
            }
        }
    }
    SubmissionListContent(state, onClick)
}

@Composable
fun SubmissionListContent(state: State<SubmissionListState>, onClick: () -> Unit) {
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
        LazyColumn(modifier = Modifier
            .padding(top = 16.dp)
            .testTag("submission_list")) {
            itemsIndexed(pagingItems) { _, item ->
                if (item?.isSelf == true) {
                    SelfTextItem(
                        modifier = Modifier.padding(top = 8.dp),
                        item
                    ) { onClick() }
                } else {
                    when (item?.urlOverriddenByDest?.processLink()) {
                        Link.ExternalLink -> {

                        }
                        is Link.ImageLink -> {
                            ImageItem(modifier = Modifier.padding(top = 8.dp), item) {
                                onClick()
                            }
                        }
                        Link.RedGifLink -> {  }
                        Link.RedditGallery -> {  }
                        is Link.ImgurGalleryLink -> {
                            if (item.imgurGalleryLinks?.size!! > 1) {
                            } else {
                            }
                        }
                        Link.RedditVideo -> {  }
                        is Link.TwitterLink -> {  }
                        is Link.VideoLink -> {  }
                        is Link.YoutubeLink -> {  }
                        Link.StreamableLink -> {  }
                        Link.GfycatLink -> {  }
                        else -> {}
                    }
                }
            }

            if (pagingItems.loadState.append == LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelfTextItem(
    modifier: Modifier = Modifier,
    item: AuthedSubmission,
    onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
            text = item.title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
            text = item.selftext ?: "",
            style = MaterialTheme.typography.bodySmall
        )
        ItemFooter(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp), author = item.authorFullname ?: "", upvote = "${item.ups ?: "?"}")
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun ImageItem(modifier: Modifier = Modifier, item: AuthedSubmission, onClick: () -> Unit) {
    if (item.preview != null) {
        val configuration = LocalConfiguration.current
        val resolution = getSuitablePreview(configuration.screenWidthDp, item.preview.images[0].resolutions)
        Card(
            onClick = { onClick() },
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp),
                text = item.title,
                style = MaterialTheme.typography.bodyMedium
            )
            GlideImage(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                model = resolution?.url,
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )
            ItemFooter(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp), author = item.authorFullname ?: "", upvote = "${item.ups ?: "?"}")
        }
    }
}

@Composable
fun ItemFooter(
    modifier: Modifier = Modifier,
    author: String,
    upvote: String,
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "by $author",
            style = MaterialTheme.typography.titleSmall
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = upvote,
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

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun SubmissionListPreview() {
    val items = flowOf(PagingData.from(createFakeData()))
    val state = mutableStateOf(SubmissionListState(items, "flexapp", "new"))
    FlexTheme {
        SubmissionListContent(state) {}
    }
}

private fun createFakeData(): List<AuthedSubmission> {
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
    )
    return listOf(post, post, post, post, post)
}

@Composable
fun SubmissionDetail(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "Submission detail title",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            textAlign = TextAlign.Center,
            text = "hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello hello",
            style = MaterialTheme.typography.bodySmall
        )
        Button(
            modifier = Modifier.padding(top = 16.dp),
            onClick = { onBack() }
        ) {
            Text(
                text = "This is another button",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}