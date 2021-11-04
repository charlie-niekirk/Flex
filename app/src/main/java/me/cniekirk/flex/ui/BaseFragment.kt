package me.cniekirk.flex.ui

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import me.cniekirk.flex.util.setCurrentScreen

open class BaseFragment(@LayoutRes id: Int) : Fragment(id) {

    override fun onResume() {
        super.onResume()
        setCurrentScreen()
    }

}