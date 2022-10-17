package me.cniekirk.flex.navigation

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Path
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.core.view.animation.PathInterpolatorCompat
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.backstack.BackStack

class DefaultAnimation<T>(
    private val alphaSpec: TransitionSpec<BackStack.State, Float>,
    private val transitionSpec: TransitionSpec<BackStack.State, Float>,
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

// Default activity animation interpolator: fast_out_v_slow_in.xml
private val path = Path().apply {
    moveTo(0f, 0f)
    cubicTo(0.05f, 0f, 0.133333f, 0.06f, 0.166666f, 0.4f)
    cubicTo(0.208333f, 0.82f, 0.25f, 1f, 1f, 1f)
}

@Composable
fun <T> rememberBackstackDefaultAnimation(
    alphaSpec: TransitionSpec<BackStack.State, Float> = {
        tween(50, 50, LinearEasing)
    },
    transitionSpec: TransitionSpec<BackStack.State, Float> = {
        val easing = Easing {
            PathInterpolatorCompat.create(path).getInterpolation(it)
        }
        tween(300, 0, easing)
    }
): ModifierTransitionHandler<T, BackStack.State> = remember {
    DefaultAnimation(transitionSpec = transitionSpec, alphaSpec = alphaSpec)
}