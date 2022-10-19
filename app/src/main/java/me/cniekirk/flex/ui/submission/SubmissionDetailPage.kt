package me.cniekirk.flex.ui.submission

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import me.cniekirk.flex.R
import me.cniekirk.flex.ui.submission.state.SubmissionDetailEffect
import me.cniekirk.flex.ui.submission.state.SubmissionDetailState
import me.cniekirk.flex.ui.viewmodel.SubmissionDetailViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun SubmissionDetail(viewModel: SubmissionDetailViewModel = viewModel()) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SubmissionDetailEffect.ShowError -> {
                Toast.makeText(context, R.string.generic_network_error, Toast.LENGTH_SHORT).show()
            }
        }
    }
    SubmissionDetailContent(state)
}

@Composable
fun SubmissionDetailContent(
    state: State<SubmissionDetailState>,
    onUpvote: () -> Unit,
    onDownvote: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

    }
}