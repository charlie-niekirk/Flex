package me.cniekirk.flex.ui.text

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import kotlin.math.ceil

fun CharSequence.getBionikSpanForText() : Spannable {
    val builder = SpannableStringBuilder()
    val text = this.toString().split(" ")

    text.forEach {
        val boldSpan = StyleSpan(Typeface.BOLD)
        val length = calculateSpanLength(it.length)
        val span = it.substring(0, length)
        val after = it.substring(length)
        builder.append(
            span, boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        builder.append("$after ")
    }

    return builder
}

private fun calculateSpanLength(length: Int) : Int {
    if (length >= 3) {
        return ceil(length * 0.4).toInt()
    } else {
        return length
    }
}