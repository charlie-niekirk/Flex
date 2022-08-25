package me.cniekirk.flex.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import me.cniekirk.flex.util.setCurrentScreen

abstract class BaseFragment<S: Any, E: Any>(@LayoutRes id: Int) : Fragment(id) {

    abstract fun render(state: S)

    abstract fun react(effect: E)

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }
}