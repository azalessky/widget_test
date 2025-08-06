package com.example.widget_test

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.core.content.ContextCompat
import com.google.android.material.R as MaterialR

class ColorsProvider(context: Context) {
    private val themedContext = android.view.ContextThemeWrapper(context, R.style.AppTheme)
    
    private fun getColorFromAttr(@AttrRes attrColor: Int): Int {
        val typedValue = TypedValue()
        themedContext.theme.resolveAttribute(attrColor, typedValue, true)

        return if (typedValue.resourceId != 0) {
            ContextCompat.getColor(themedContext, typedValue.resourceId)
        } else {
            typedValue.data
        }
    }

    val colorOnSurface: Int
        get() = getColorFromAttr(MaterialR.attr.colorOnSurface)

    val colorOnTertiaryContainer: Int
        get() = getColorFromAttr(MaterialR.attr.colorOnTertiaryContainer)
}
