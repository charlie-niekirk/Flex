package me.cniekirk.flex.ui.util

interface ViewRenderer<in State: Any> {

    fun render(state: State)
}