package me.cniekirk.flex.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.bumble.appyx.core.plugin.NodeAware
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.cniekirk.flex.R
import me.cniekirk.flex.navigation.FlexRootNode
import me.cniekirk.flex.ui.compose.styles.FlexTheme
import me.cniekirk.flex.ui.submission.model.UiSubmission

@AndroidEntryPoint
class ContainerActivity : NodeActivity() {

    lateinit var flexRootNode: FlexRootNode

    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlexTheme {
                NodeHost(integrationPoint = appyxIntegrationPoint) {
                    FlexRootNode(
                        buildContext = it,
                        plugins = listOf(object : NodeAware<FlexRootNode> {
                            override fun init(node: FlexRootNode) {
                                flexRootNode = node
                                handleDeeplink(intent = intent)
                            }

                            override val node: FlexRootNode
                                get() = flexRootNode
                        })
                    )
                }
            }
        }
    }

    private fun handleDeeplink(intent: Intent) {
        val uiSubmission = intent.parcelable<UiSubmission>("submission")
        uiSubmission?.let {
            // Do the navigation
            deeplinkSubmissionDetails(uiSubmission)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDeeplink(intent)
    }

    private fun executeNavigation(navigationAction: suspend () -> Unit) {
        job?.cancel()
        job = lifecycleScope.launch {
            navigationAction()
        }
    }

    private fun deeplinkSubmissionDetails(uiSubmission: UiSubmission) {
        executeNavigation {
            flexRootNode.attachSubmissionDetail(uiSubmission)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}

inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}
