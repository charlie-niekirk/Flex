package me.cniekirk.flex.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import me.cniekirk.flex.R
import timber.log.Timber

class AspectRatioImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): AppCompatImageView(context, attrs, defStyleAttr) {

    var ratio: Float = DEFAULT_RATIO

    init {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.AspectRatioImageView).apply {
                ratio = getFloat(R.styleable.AspectRatioImageView_ari_ratio, DEFAULT_RATIO)
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = measuredWidth
        var height = measuredHeight

        Timber.d("SETTING HEIGHT: ${(width * ratio).toInt()}")
        when {
            width > 0 -> height = (width * ratio).toInt()
//            height > 0 -> width = (height / ratio).toInt()
            else -> return
        }

        setMeasuredDimension(width, height)
    }

    companion object {
        const val DEFAULT_RATIO = 1F
    }
}