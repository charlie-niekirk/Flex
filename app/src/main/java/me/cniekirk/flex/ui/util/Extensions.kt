package me.cniekirk.flex.ui.util

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.fragment.findNavController
import kotlin.math.absoluteValue

private const val SECOND_MILLIS: Long = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val MONTH_MILLIS = 30 * DAY_MILLIS
private const val YEAR_MILLIS = 12 * MONTH_MILLIS

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

fun Long.getElapsedTime(adjust: Boolean = true): String {
    val now = System.currentTimeMillis()
    val diff = if (adjust) {
        now - (this * 1000)
    } else {
        (now - this).absoluteValue
    }
    return when {
        diff < MINUTE_MILLIS -> {
            "now"
        }
        diff < 2 * MINUTE_MILLIS -> {
            "1m"
        }
        diff < 50 * MINUTE_MILLIS -> {
            "${diff / MINUTE_MILLIS}m"
        }
        diff < 120 * MINUTE_MILLIS -> {
            "1h"
        }
        diff < 24 * HOUR_MILLIS -> {
            "${diff / HOUR_MILLIS}h"
        }
        diff < 48 * HOUR_MILLIS -> {
            "1d"
        }
        diff < MONTH_MILLIS -> {
            "${diff / DAY_MILLIS}d"
        }
        diff < 2 * MONTH_MILLIS -> {
            "1M"
        }
        diff < YEAR_MILLIS -> {
            "${diff / MONTH_MILLIS}M"
        }
        diff < 2 * YEAR_MILLIS -> {
            "1Y"
        }
        else -> {
            "${diff / YEAR_MILLIS}Y"
        }
    }
}