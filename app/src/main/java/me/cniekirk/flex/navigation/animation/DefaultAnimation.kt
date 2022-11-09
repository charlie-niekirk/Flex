package me.cniekirk.flex.navigation.animation

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
    private val scaleSpec: TransitionSpec<BackStack.State, Float>,
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<T, BackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<T, BackStack.State>
    ): Modifier = modifier.composed {
        val scale = transition.animateFloat(
            transitionSpec = scaleSpec,
            targetValueByState = { it.toProps().scale },
            label = ""
        )

        val alpha = transition.animateFloat(
            transitionSpec = alphaSpec,
            targetValueByState = { it.toProps().alpha }, label = ""
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
    scaleSpec: TransitionSpec<BackStack.State, Float> = {
        val easing = Easing {
            PathInterpolatorCompat.create(path).getInterpolation(it)
        }
        tween(300, 0, easing)
    }
): ModifierTransitionHandler<T, BackStack.State> = remember {
    DefaultAnimation(scaleSpec = scaleSpec, alphaSpec = alphaSpec)
}

private data class Props(
    val alpha: Float = 1f,
    val scale: Float = 1f
)

private val created = Props()
private val active = created.copy(alpha = 1f, scale = 1f)
private val stashed = active.copy(alpha = 0f, scale = 1.1f)
private val destroyed = stashed

private fun BackStack.State.toProps(): Props =
    when (this) {
        BackStack.State.CREATED -> created
        BackStack.State.ACTIVE -> active
        BackStack.State.STASHED -> stashed
        BackStack.State.DESTROYED -> destroyed
    }