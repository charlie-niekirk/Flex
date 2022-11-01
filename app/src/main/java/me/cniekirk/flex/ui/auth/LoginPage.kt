package me.cniekirk.flex.ui.auth

import android.graphics.Bitmap
import android.net.UrlQuerySanitizer
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import me.cniekirk.flex.ui.viewmodel.AuthenticationViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import me.cniekirk.flex.ui.auth.state.LoginSideEffect
import me.cniekirk.flex.ui.auth.state.LoginState
import me.cniekirk.flex.util.isRedirectUri
import me.cniekirk.flex.util.provideAuthorizeUrl
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LoginPage(
    viewModel: AuthenticationViewModel = viewModel(),
    onLoginSuccess: () -> Unit
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
                onLoginSuccess()
            }
        }
    }

    LoginPageContent(state = state, onCodeIntercept = viewModel::onCodeIntercept)
}

@Composable
fun LoginPageContent(
    state: State<LoginState>,
    onCodeIntercept: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
}