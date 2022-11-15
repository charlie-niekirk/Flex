package me.cniekirk.flex.ui.search

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.cniekirk.flex.R
import me.cniekirk.flex.data.remote.model.reddit.subreddit.Subreddit
import me.cniekirk.flex.ui.compose.FlexTextField
import me.cniekirk.flex.ui.compose.styles.FlexTheme
import me.cniekirk.flex.ui.search.mvi.SearchSideEffect
import me.cniekirk.flex.ui.search.mvi.SearchState
import me.cniekirk.flex.ui.viewmodel.SearchViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun SearchPage(
    viewModel: SearchViewModel = viewModel(),
    subredditSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SearchSideEffect.Error -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            is SearchSideEffect.SubredditSelected -> {
                subredditSelected(sideEffect.subreddit)
            }
        }
    }
    SearchPageContent(state, viewModel::onSearch, viewModel::randomClicked) { subredditSelected(it) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPageContent(
    state: State<SearchState>,
    onSearchChange: (String) -> Unit,
    onRandomSelected: (String) -> Unit,
    subredditSelected: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp),
            text = stringResource(id = R.string.search_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        FlexTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            value = text,
            onValueChange = {
                onSearchChange(it.text)
                text = it },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "") },
            shape = RoundedCornerShape(40.dp)
        )
        Button(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            onClick = { onRandomSelected("random") }
        ) {
            Text(text = "Random")
        }
        Button(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                .fillMaxWidth(),
            onClick = { onRandomSelected("randnsfw") }
        ) {
            Text(text = "RandNSFW")
        }

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            items(state.value.searchResults.size) {
                state.value.searchResults.forEach { subreddit ->
                    SubredditItem(subreddit) {
                        subredditSelected(it)
                    }
                }
            }

        }
    }
}

@Composable
fun SubredditItem(
    subreddit: Subreddit,
    subredditSelected: (String) -> Unit
) {
    Column {
        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable { subreddit.displayName?.let(subredditSelected) }
            .padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subreddit.displayName ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                modifier = Modifier.padding(top = 2.dp),
                text = subreddit.description ?: "",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
        Divider()
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun SearchPagePreview() {
    val state = mutableStateOf(SearchState())
    FlexTheme {
        SearchPageContent(state, onSearchChange = {}, onRandomSelected = {}, subredditSelected = {})
    }
}
