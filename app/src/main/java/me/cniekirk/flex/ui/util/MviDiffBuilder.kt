package me.cniekirk.flex.ui.util

open class MviDiffBuilder<State: Any> {

    val renderers = mutableListOf<ViewRenderer<State>>()

    inline fun <T> diff(
        crossinline get: (State) -> T,
        crossinline compare: (new: T, old: T) -> Boolean = { a, b -> a == b },
        crossinline set: (T) -> Unit
    ) {
        renderers +=
            object : ViewRenderer<State> {
                private var oldState: T? = null

                override fun render(state: State) {
                    val newState = get(state)
                    val oldState = oldState
                    this.oldState = newState

                    if ((oldState == null) || !compare(newState, oldState)) {
                        set(newState)
                    }
                }
            }
    }
}

inline fun <State : Any> diff(block: MviDiffBuilder<State>.() -> Unit): ViewRenderer<State> {
    val builder =
        object : MviDiffBuilder<State>(), ViewRenderer<State> {
            override fun render(state: State) {
                renderers.forEach { it.render(state) }
            }
        }

    builder.block()

    return builder
}