package me.cniekirk.flex.ui.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import com.bumble.appyx.core.integration.NodeHost
import com.bumble.appyx.core.integrationpoint.NodeActivity
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import me.cniekirk.flex.navigation.FlexRootNode
import me.cniekirk.flex.ui.compose.styles.FlexTheme

@AndroidEntryPoint
class ContainerActivity : NodeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlexTheme {
                NodeHost(integrationPoint = appyxIntegrationPoint) {
                    FlexRootNode(buildContext = it)
                }
            }
        }
    }
}