package me.cniekirk.flex.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import me.cniekirk.flex.R

class SettingsSectionItem @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributeSet, defStyleAttr) {

    private val titleView: TextView
    private val subtitleView: TextView
    private val iconView: ImageView

    private var title: String = ""
    private var subtitle: String = ""
    private var icon: Int = 0

    init {
        attributeSet?.let {
            context.obtainStyledAttributes(it, R.styleable.SettingsSectionItem).apply {
                title = getString(R.styleable.SettingsSectionItem_section_title) ?: ""
                subtitle = getString(R.styleable.SettingsSectionItem_section_subtitle) ?: ""
                icon = getResourceId(R.styleable.SettingsSectionItem_section_icon, 0)
                recycle()
            }
        }

        val root = LayoutInflater.from(context).inflate(R.layout.settings_section_item, this)
        titleView = root.findViewById(R.id.settings_section_title)
        subtitleView = root.findViewById(R.id.settings_section_subtitle)
        iconView = root.findViewById(R.id.settings_section_icon)
        titleView.text = title
        subtitleView.text = subtitle
        iconView.setImageDrawable(ContextCompat.getDrawable(context, icon))
    }

}