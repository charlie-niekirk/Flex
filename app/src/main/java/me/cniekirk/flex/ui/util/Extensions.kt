package me.cniekirk.flex.ui.util

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController

fun <T : Any> Fragment.onDialogResult(
    @IdRes destination: Int,
    argumentName: String,
    body: (T?) -> Unit
) {
    val navBackStackEntry = findNavController().getBackStackEntry(destination)
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME
            && navBackStackEntry.savedStateHandle.contains(argumentName)) {
            val result = navBackStackEntry.savedStateHandle.get<T>(argumentName)
            body(result)
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)

    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
}

fun <T : Any> Fragment.setResult(argumentName: String, result: T) {
    findNavController().currentBackStackEntry?.savedStateHandle?.set(argumentName, result)
}

fun <T : Any> Fragment.onFragmentResult(
    argumentName: String,
    body: (T?) -> Unit
) {
    findNavController().currentBackStackEntry?.savedStateHandle
        ?.getLiveData<T>(argumentName)?.observe(viewLifecycleOwner) { result ->
        body(result)
    }
}