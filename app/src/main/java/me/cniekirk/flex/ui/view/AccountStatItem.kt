package me.cniekirk.flex.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import me.cniekirk.flex.R

class AccountStatItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val titleText: TextView
    private val valueText: TextView

    private var name: String = ""
    private var value: String = ""

    init {
        attributeSet?.let {
            context.obtainStyledAttributes(it, R.styleable.AccountStatItem).apply {
                name = getString(R.styleable.AccountStatItem_stat_name) ?: ""
                value = getString(R.styleable.AccountStatItem_stat_value)?: ""
                recycle()
            }
        }

        val root = LayoutInflater.from(context).inflate(R.layout.account_stat_item, this)
        titleText = root.findViewById(R.id.stat_title)
        valueText = root.findViewById(R.id.stat_value)
        titleText.text = name
        valueText.text = value
    }

    fun setValue(newValue: String) {
        value = newValue
        valueText.text = value
    }
}