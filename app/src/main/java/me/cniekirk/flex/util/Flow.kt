package me.cniekirk.flex.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Scope flow collection to the appropriate [LifecycleOwner] depending on call site
 *
 * @param flow the flow being collected
 * @param block the function to call on collection
 */
fun <T: Any?> LifecycleOwner.observe(flow: Flow<T>, block: suspend (T) -> Unit) {
    val lifecycleOwner = if (this is Fragment) viewLifecycleOwner else this
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(block)
        }
    }
}