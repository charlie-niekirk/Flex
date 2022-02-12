package me.cniekirk.flex.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import timber.log.Timber

fun View.bitmap(block: (Bitmap) -> Unit) {
    Timber.d("View stats [width: $width, height: $height, measured width: $measuredWidth, measured height: $measuredHeight]")
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    block(bitmap)
}