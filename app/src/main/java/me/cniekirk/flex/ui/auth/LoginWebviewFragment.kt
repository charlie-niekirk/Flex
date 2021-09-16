package me.cniekirk.flex.ui.auth

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.UrlQuerySanitizer
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.R
import me.cniekirk.flex.databinding.LoginWebviewFragmentBinding
import me.cniekirk.flex.domain.RedditResult
import me.cniekirk.flex.ui.viewmodel.AuthenticationViewModel
import me.cniekirk.flex.util.isRedirectUri
import me.cniekirk.flex.util.observe
import me.cniekirk.flex.util.provideAuthorizeUrl
import timber.log.Timber

@AndroidEntryPoint
class LoginWebviewFragment : Fragment(R.layout.login_webview_fragment) {

    private var binding: LoginWebviewFragmentBinding? = null
    private val viewModel by viewModels<AuthenticationViewModel>()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = LoginWebviewFragmentBinding.bind(view)

        observe(viewModel.loginState) {
            when (it) {
                is RedditResult.Error -> {
                    Toast.makeText(requireContext(), R.string.auth_error, Toast.LENGTH_SHORT).show()
                    binding?.root?.findNavController()?.popBackStack()
                }
                RedditResult.Loading -> {}
                is RedditResult.Success -> {
                    binding?.root?.findNavController()?.popBackStack()
                }
            }
        }

        binding?.apply {
            loginWebview.apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        if (url.isRedirectUri()) {
                            this@apply.stopLoading()
                            val parsed = UrlQuerySanitizer().apply {
                                allowUnregisteredParamaters = true
                                parseUrl(url)
                            }
                            val code = parsed.getValue("code")
                            viewModel.onCodeIntercepted(code.substring(0, code.lastIndexOf("#")))
                        }
                    }
                }.apply { settings.javaScriptEnabled = true }
                loadUrl(provideAuthorizeUrl())
            }
            webviewToolbar.setNavigationOnClickListener {
                it?.findNavController()?.popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}