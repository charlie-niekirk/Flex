package me.cniekirk.flex.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View

fun View.bitmap(block: (Bitmap) -> Unit) {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    draw(canvas)
    block(bitmap)
}