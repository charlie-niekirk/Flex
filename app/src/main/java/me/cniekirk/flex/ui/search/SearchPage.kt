package me.cniekirk.flex.ui.search

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.cniekirk.flex.R
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
            is SearchSideEffect.RandomSelected -> {
                subredditSelected(sideEffect.subreddit)
            }
        }
    }
    SearchPageContent(state, viewModel::searchSubreddit)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPageContent(
    state: State<SearchState>,
    onSearchChange: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf("") }
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
            value = TextFieldValue(text),
            onValueChange = {
                onSearchChange(it.text)
                text = it.text },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "") },
            shape = RoundedCornerShape(40.dp)
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun SearchPagePreview() {
    val state = mutableStateOf(SearchState())
    FlexTheme {
        SearchPageContent(state, onSearchChange = {})
    }
}
