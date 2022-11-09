package me.cniekirk.flex.navigation.animation

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import com.bumble.appyx.core.navigation.transition.ModifierTransitionHandler
import com.bumble.appyx.core.navigation.transition.TransitionDescriptor
import com.bumble.appyx.core.navigation.transition.TransitionSpec
import com.bumble.appyx.navmodel.backstack.BackStack
import kotlin.math.roundToInt

class SheetAnimation<T>(
    private val offsetSpec: TransitionSpec<BackStack.State, Offset>,
    override val clipToBounds: Boolean = false
) : ModifierTransitionHandler<T, BackStack.State>() {

    @SuppressLint("ModifierFactoryExtensionFunction")
    override fun createModifier(
        modifier: Modifier,
        transition: Transition<BackStack.State>,
        descriptor: TransitionDescriptor<T, BackStack.State>
    ): Modifier = modifier.composed {

        val height = descriptor.params.bounds.height.value

        val offset by transition.animateOffset(
            transitionSpec = offsetSpec,
            targetValueByState = { it.toProps(height).offset },
            label = ""
        )

        offset {
            IntOffset(
                x = (offset.x * density).roundToInt(),
                y = (offset.y * density).roundToInt()
            )
        }
    }
}

@Composable
fun <T> rememberSheetAnimation(
    offsetSpec: TransitionSpec<BackStack.State, Offset> = {
        tween(500, 0, FastOutSlowInEasing)
    }
): ModifierTransitionHandler<T, BackStack.State> = remember {
    SheetAnimation(offsetSpec = offsetSpec)
}

private data class SheetProps(
    val offset: Offset = Offset.Zero
)

private val created = SheetProps()
private val active = created
private val stashed = active
private val destroyed = stashed

private fun BackStack.State.toProps(height: Float): SheetProps =
    when (this) {
        BackStack.State.CREATED -> created.copy(offset = Offset(0f, 2f * height))
        BackStack.State.ACTIVE -> active
        BackStack.State.STASHED -> stashed
        BackStack.State.DESTROYED -> destroyed
    }