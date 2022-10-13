package me.cniekirk.flex.ui.auth

//import android.annotation.SuppressLint
//import android.graphics.Bitmap
//import android.net.UrlQuerySanitizer
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.widget.Toast
//import androidx.fragment.app.viewModels
//import androidx.navigation.NavGraph
//import androidx.navigation.fragment.findNavController
//import androidx.navigation.get
//import dagger.hilt.android.AndroidEntryPoint
//import me.cniekirk.flex.R
//import me.cniekirk.flex.databinding.LoginWebviewFragmentBinding
//import me.cniekirk.flex.domain.RedditResult
//import me.cniekirk.flex.ui.dialog.FullscreenDialog
//import me.cniekirk.flex.ui.viewmodel.AuthenticationViewModel
//import me.cniekirk.flex.util.isRedirectUri
//import me.cniekirk.flex.util.observe
//import me.cniekirk.flex.util.provideAuthorizeUrl
//import timber.log.Timber
//
//@AndroidEntryPoint
//class LoginWebviewFragment : FullscreenDialog() {
//
//    private var binding: LoginWebviewFragmentBinding? = null
//    private val viewModel by viewModels<AuthenticationViewModel>()
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = LoginWebviewFragmentBinding.inflate(inflater, container, false)
//        return binding?.root
//    }
//
//    @SuppressLint("SetJavaScriptEnabled")
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        binding = LoginWebviewFragmentBinding.bind(view)
//
//        observe(viewModel.loginState) {
//            when (it) {
//                RedditResult.Loading -> { }
//                is RedditResult.Success -> {
//                    dismiss()
//                    (findNavController().graph[R.id.accountNavGraph] as NavGraph).setStartDestination(R.id.authenticatedAccountFragment)
//                    findNavController().navigate(R.id.authenticatedAccountFragment)
//                }
//                is RedditResult.Error -> {
//                    Toast.makeText(requireContext(), R.string.login_failed, Toast.LENGTH_SHORT).show()
//                    dismiss()
//                }
//            }
//        }
//
//        binding?.apply {
//            loginWebview.apply {
//                webViewClient = object : WebViewClient() {
//                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
//                        if (url.isRedirectUri()) {
//                            this@apply.stopLoading()
//                            val parsed = UrlQuerySanitizer().apply {
//                                allowUnregisteredParamaters = true
//                                parseUrl(url)
//                            }
//                            val code = parsed.getValue("code")
//                            viewModel.onCodeIntercepted(code.substring(0, code.lastIndexOf("#")))
//                        }
//                    }
//                }.apply { settings.javaScriptEnabled = true }
//                loadUrl(provideAuthorizeUrl())
//            }
//            webviewToolbar.setNavigationOnClickListener {
//                findNavController().navigateUp()
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        binding = null
//        super.onDestroyView()
//    }
//}