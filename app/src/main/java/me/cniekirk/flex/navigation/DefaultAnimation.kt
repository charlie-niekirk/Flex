package me.cniekirk.flex.navigation

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.backstack.BackStack

class DefaultAnimation<T>(
    private val alphaSpec: TransitionSpec<BackStack.State, Float> = {
        tween(50, 50, LinearEasing)
    },
    private val transitionSpec: TransitionSpec<BackStack.State, Float> = {
        tween(400, 0, FastOutSlowInEasing)
    },
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<T, BackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<T, BackStack.State>
    ): Modifier = modifier.composed {
        val scale = transition.animateFloat(
            transitionSpec = transitionSpec,
            targetValueByState = {
                when (it) {
                    BackStack.State.ACTIVE -> 1f
                    BackStack.State.STASHED -> 1.1f
                    else -> 0.85f
                }
            }, label = ""
        )

        val alpha = transition.animateFloat(
            transitionSpec = alphaSpec,
            targetValueByState = {
                when (it) {
                    BackStack.State.ACTIVE -> 1f
                    else -> 0f
                }
            }, label = ""
        )

        alpha(alpha.value).scale(scale.value)
    }
}

@Composable
fun <T> rememberBackstackDefaultAnimation(
    transitionSpec: TransitionSpec<BackStack.State, Float> = { spring() }
): ModifierTransitionHandler<T, BackStack.State> = remember {
    DefaultAnimation(transitionSpec = transitionSpec)
}