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

    val itemText: Int
        get() = getColorFromAttr(MaterialR.attr.colorOnSurface)

    val itemBackground: Int
        get() = getColorFromAttr(MaterialR.attr.colorSurface)

    val selectedItemText: Int
        get() = getColorFromAttr(MaterialR.attr.colorOnTertiaryContainer)

    val selectedItemBackground: Int
        get() = getColorFromAttr(MaterialR.attr.colorTertiaryContainer)

    val completedItemText: Int
        get() = getColorFromAttr(MaterialR.attr.colorOnSurfaceVariant)

    val completedItemBackground: Int
        get() = getColorFromAttr(MaterialR.attr.colorSurfaceVariant)

}
