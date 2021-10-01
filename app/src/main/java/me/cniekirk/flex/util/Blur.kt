package me.cniekirk.flex.util

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.FragmentActivity
import com.google.android.renderscript.Toolkit

fun View.bitmap(activity: FragmentActivity, block: (Bitmap) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // PixelCopy API
        activity.window?.let { window ->
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val locationOfViewInWindow = IntArray(2)
            getLocationInWindow(locationOfViewInWindow)
            try {
                PixelCopy.request(window, Rect(
                    locationOfViewInWindow[0],
                    locationOfViewInWindow[1],
                    locationOfViewInWindow[0] + width,
                    locationOfViewInWindow[1] + height), bitmap, { copyResult ->
                    if (copyResult == PixelCopy.SUCCESS) {
                        block(bitmap)
                    }
                }, Handler(Looper.getMainLooper()))
            } catch (e: IllegalArgumentException) {
                // PixelCopy may throw IllegalArgumentException, make sure to handle it
                e.printStackTrace()
            }
        }
    } else {
        // Canvas
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        block(bitmap)
    }
}