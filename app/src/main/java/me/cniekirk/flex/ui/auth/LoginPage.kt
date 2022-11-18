package me.cniekirk.flex.ui.auth

import android.graphics.Bitmap
import android.net.UrlQuerySanitizer
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import me.cniekirk.flex.ui.viewmodel.AuthenticationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import me.cniekirk.flex.ui.auth.state.LoginSideEffect
import me.cniekirk.flex.ui.auth.state.LoginState
import me.cniekirk.flex.ui.auth.state.Stage
import me.cniekirk.flex.util.isRedirectUri
import me.cniekirk.flex.util.provideAuthorizeUrl
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginPage(
    viewModel: AuthenticationViewModel = viewModel()
) {
    val context = LocalContext.current
    val state = viewModel.collectAsState()

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.Error -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            LoginSideEffect.LoginSuccess -> {
                Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AuthPageContent(state = state, onCodeIntercept = viewModel::onCodeIntercept)
}

@Composable
fun AuthPageContent(
    state: State<LoginState>,
    onCodeIntercept: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (state.value.stage) {
            Stage.PRE_LOGIN -> {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                                    if (url.isRedirectUri()) {
                                        this@apply.stopLoading()
                                        val parsed = UrlQuerySanitizer().apply {
                                            allowUnregisteredParamaters = true
                                            parseUrl(url)
                                        }
                                        val code = parsed.getValue("code")
                                        onCodeIntercept(code.substring(0, code.lastIndexOf("#")))
                                    }
                                }
                            }.apply { settings.javaScriptEnabled = true }
                            loadUrl(provideAuthorizeUrl())
                        }
                    }
                )
            }
            Stage.POST_LOGIN -> {
                Text(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .testTag("username"),
                    text = state.value.redditUser.name ?: "",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            null -> {
                // Loading
            }
        }
    }
}