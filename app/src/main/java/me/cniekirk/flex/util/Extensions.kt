package me.cniekirk.flex.util

import android.icu.text.CompactDecimalFormat
import android.icu.util.ULocale


fun Int.condense(): String {
    val cdf = CompactDecimalFormat.getInstance(ULocale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT)
    return cdf.format(this)
}

fun String.toPreviewUrl(): String = this.replace("&amp;", "&")

fun String.selfTextPreview(): String {
    return replace("&amp;#x200B;", "").
    replace("&#x200B;", "").
    replace("#", "").
    replace("\n", " ").trim(' ')
}

private const val SECOND_MILLIS: Long = 1000
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS
private const val MONTH_MILLIS = 30 * DAY_MILLIS
private const val YEAR_MILLIS = 12 * MONTH_MILLIS

fun Long.getElapsedTime(): String {
    val now = System.currentTimeMillis()
    val diff = now - (this * 1000)
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