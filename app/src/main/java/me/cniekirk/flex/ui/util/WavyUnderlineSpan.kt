package me.cniekirk.flex.ui.util

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan
import timber.log.Timber
import kotlin.math.sin

class WavyUnderlineSpan(
    private val waveColor: Int
) : DynamicDrawableSpan() {

    private var width = 0

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val painter = Paint(paint).apply {
            color = waveColor
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 6.0f
            isAntiAlias = true
            flags = Paint.ANTI_ALIAS_FLAG
        }
        val height = (bottom - y) / 6.5

        canvas.drawText(text?.subSequence(start, end).toString(), x, y.toFloat(), painter.apply { style = Paint.Style.FILL })

        var i = x
        while (i <= x + width) {
            val dy = sin((i * SCALE_X)) * height
            canvas.drawPoint(i, (dy + height).toFloat() + y + 17, painter)
            i += STEP_X
        }
    }

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        width = paint.measureText(text, start, end).toInt()
        return width
    }

    override fun getDrawable(): Drawable? = null

    companion object {
        const val PERIODS_TO_SHOW = 3
        const val STEP_X = 0.1f
        const val SCALE_X = 0.06
    }
}