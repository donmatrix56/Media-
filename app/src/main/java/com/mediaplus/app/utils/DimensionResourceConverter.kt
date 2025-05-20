package com.mediaplus.app.utils

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Utility to help with transitioning from static dimension resources to
 * programmatic resolution-based dimensions.
 * 
 * This class provides helper methods that can replace common dimension resource calls
 * with programmatic alternatives.
 */
class DimensionResourceConverter(private val context: Context) {
    
    private val dimensionUtils = DimensionUtils.getInstance(context)
    private val displayUtils = DisplayUtils.getInstance(context)
    
    /**
     * Mapping of common dimension resource names to equivalent programmatic values
     */
    fun convertDimenToPixels(dimenResName: String): Int {
        return when (dimenResName) {
            // Margins
            "margin_small" -> dimensionUtils.getMarginSmall()
            "margin_medium" -> dimensionUtils.getMarginMedium()
            "margin_large" -> dimensionUtils.getMarginLarge()
            "margin_xlarge" -> dimensionUtils.getMarginXLarge()
            "margin_xxlarge" -> dimensionUtils.getMarginXXLarge()
            
            // Paddings
            "padding_small" -> dimensionUtils.getMarginSmall()
            "padding_medium" -> dimensionUtils.getMarginMedium()
            "padding_large" -> dimensionUtils.getMarginLarge()
            
            // Card dimensions
            "card_corner_radius" -> dimensionUtils.getScaledCornerRadius(8f)
            "card_elevation" -> displayUtils.getProportionalDimension(2f)
            "card_margin" -> dimensionUtils.getMarginSmall()
            
            // Video item dimensions
            "video_card_height" -> dimensionUtils.getVideoCardHeight()
            "gradient_height" -> displayUtils.getProportionalDimension(80f)
            
            // Icon sizes
            "icon_size_small" -> dimensionUtils.getIconSizeSmall()
            "icon_size_medium" -> dimensionUtils.getIconSizeMedium()
            "icon_size_large" -> dimensionUtils.getIconSizeLarge()
            "icon_size_xlarge" -> dimensionUtils.getIconSizeXLarge()
            
            // Control heights
            "control_height_small" -> com.mediaplus.app.utils.ResponsiveUIHelper.getInstance(context).getControlHeightSmall()
            "control_height_medium" -> com.mediaplus.app.utils.ResponsiveUIHelper.getInstance(context).getControlHeightMedium()
            "control_height_large" -> dimensionUtils.getItemHeightLarge()
            
            // Default - use a small value as fallback
            else -> displayUtils.getProportionalDimension(8f)
        }
    }
    
    /**
     * Get the equivalent column count for grid layouts based on screen size
     * 
     * @param integerResName The name of the integer resource (e.g., "videos_grid_columns")
     * @return The optimal column count for the current device
     */
    fun getGridColumnCount(integerResName: String): Int {
        return when (integerResName) {
            "videos_grid_columns" -> dimensionUtils.getVideoGridColumns()
            "music_grid_columns" -> dimensionUtils.getAudioGridColumns()
            else -> displayUtils.getOptimalColumnCount(2, 3)
        }
    }
    
    /**
     * Helper method to convert TextView that uses dimension resources to 
     * programmatic responsive dimensions
     * 
     * @param textView The TextView to convert
     */
    fun applyResponsiveTextSize(textView: TextView) {
        val tag = textView.tag?.toString() ?: ""
        when {
            tag.contains("title") || textView.id.toString().contains("title") -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeHeading())
            }
            tag.contains("subtitle") -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeLarge())
            }
            else -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeMedium())
            }
        }
    }
    
    /**
     * Helper method to set up a RecyclerView with responsive grid layout
     * 
     * @param recyclerView The RecyclerView to set up
     * @param integerResName The name of the integer resource that defined column count
     */
    fun setupResponsiveGridRecyclerView(recyclerView: RecyclerView, integerResName: String) {
        val columns = getGridColumnCount(integerResName)
        recyclerView.layoutManager = GridLayoutManager(context, columns)
        
        // Apply responsive padding
        val padding = dimensionUtils.getMarginMedium()
        recyclerView.setPadding(padding, padding, padding, padding)
    }
    
    companion object {
        @Volatile
        private var INSTANCE: DimensionResourceConverter? = null
        
        fun getInstance(context: Context): DimensionResourceConverter {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DimensionResourceConverter(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
