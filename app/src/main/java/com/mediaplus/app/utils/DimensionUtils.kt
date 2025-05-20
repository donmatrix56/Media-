package com.mediaplus.app.utils

import android.content.Context
import android.util.TypedValue
import android.view.ViewGroup

/**
 * Utility class for responsive dimensions that adapt to screen size.
 * This provides dynamic dimensions based on screen metrics rather than
 * hardcoded values, ensuring UI elements scale appropriately across devices.
 */
class DimensionUtils private constructor(private val context: Context) {
    
    private val displayUtils = DisplayUtils.getInstance(context)
    
    // Cache to avoid repeated calculations
    private val cachedDimensions = mutableMapOf<String, Int>()
    
    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: DimensionUtils? = null
        
        fun getInstance(context: Context): DimensionUtils {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: DimensionUtils(context.applicationContext).also { INSTANCE = it }
            }
        }
        
        // Base dimensions for a reference device
        
        // Margins
        private val BASE_MARGIN_TINY = 2f
        private val BASE_MARGIN_SMALL = 4f
        private val BASE_MARGIN_MEDIUM = 8f
        private val BASE_MARGIN_LARGE = 16f
        private val BASE_MARGIN_XLARGE = 24f
        private val BASE_MARGIN_XXLARGE = 32f
        
        // Text sizes
        private val BASE_TEXT_TINY = 10f
        private val BASE_TEXT_SMALL = 12f
        private val BASE_TEXT_MEDIUM = 14f
        private val BASE_TEXT_LARGE = 16f
        private val BASE_TEXT_XLARGE = 18f
        private val BASE_TEXT_XXLARGE = 20f
        private val BASE_TEXT_HEADING = 24f
        private val BASE_TEXT_DISPLAY = 34f
        
        // Icon sizes
        private val BASE_ICON_SMALL = 24f
        private val BASE_ICON_MEDIUM = 32f
        private val BASE_ICON_LARGE = 48f
        private val BASE_ICON_XLARGE = 64f
        
        // Component sizes
        private val BASE_BUTTON_HEIGHT = 48f
        private val BASE_ITEM_HEIGHT_SMALL = 48f
        private val BASE_ITEM_HEIGHT_MEDIUM = 72f
        private val BASE_ITEM_HEIGHT_LARGE = 88f
        
        // Grid item dimensions
        private val BASE_VIDEO_CARD_HEIGHT = 200f
        private val BASE_AUDIO_ITEM_HEIGHT = 72f
        
        // Screen size breakpoints (smallest dimension in dp)
        private val BREAKPOINT_PHONE = 480f
        private val BREAKPOINT_TABLET = 600f
        private val BREAKPOINT_LARGE_TABLET = 840f
    }
    
    // Get scaled margins
    fun getMarginTiny(): Int = getScaledDimension("margin_tiny", BASE_MARGIN_TINY, 0.8f)
    fun getMarginSmall(): Int = getScaledDimension("margin_small", BASE_MARGIN_SMALL, 0.8f)
    fun getMarginMedium(): Int = getScaledDimension("margin_medium", BASE_MARGIN_MEDIUM, 0.8f)
    fun getMarginLarge(): Int = getScaledDimension("margin_large", BASE_MARGIN_LARGE, 0.8f)
    fun getMarginXLarge(): Int = getScaledDimension("margin_xlarge", BASE_MARGIN_XLARGE, 0.7f)
    fun getMarginXXLarge(): Int = getScaledDimension("margin_xxlarge", BASE_MARGIN_XXLARGE, 0.7f)
    
    // Get scaled text sizes
    fun getTextSizeTiny(): Float = getScaledTextSize("text_tiny", BASE_TEXT_TINY, 0.9f)
    fun getTextSizeSmall(): Float = getScaledTextSize("text_small", BASE_TEXT_SMALL, 0.9f)
    fun getTextSizeMedium(): Float = getScaledTextSize("text_medium", BASE_TEXT_MEDIUM, 0.9f)
    fun getTextSizeLarge(): Float = getScaledTextSize("text_large", BASE_TEXT_LARGE, 0.9f)
    fun getTextSizeXLarge(): Float = getScaledTextSize("text_xlarge", BASE_TEXT_XLARGE, 0.85f)
    fun getTextSizeXXLarge(): Float = getScaledTextSize("text_xxlarge", BASE_TEXT_XXLARGE, 0.85f)
    fun getTextSizeHeading(): Float = getScaledTextSize("text_heading", BASE_TEXT_HEADING, 0.8f)
    fun getTextSizeDisplay(): Float = getScaledTextSize("text_display", BASE_TEXT_DISPLAY, 0.75f)
    
    // Get scaled icon sizes
    fun getIconSizeSmall(): Int = getScaledDimension("icon_small", BASE_ICON_SMALL, 0.9f)
    fun getIconSizeMedium(): Int = getScaledDimension("icon_medium", BASE_ICON_MEDIUM, 0.85f)
    fun getIconSizeLarge(): Int = getScaledDimension("icon_large", BASE_ICON_LARGE, 0.8f)
    fun getIconSizeXLarge(): Int = getScaledDimension("icon_xlarge", BASE_ICON_XLARGE, 0.75f)
    
    // Get scaled component sizes
    fun getButtonHeight(): Int = getScaledDimension("button_height", BASE_BUTTON_HEIGHT, 0.9f)
    fun getItemHeightSmall(): Int = getScaledDimension("item_height_small", BASE_ITEM_HEIGHT_SMALL, 0.9f)
    fun getItemHeightMedium(): Int = getScaledDimension("item_height_medium", BASE_ITEM_HEIGHT_MEDIUM, 0.85f)
    fun getItemHeightLarge(): Int = getScaledDimension("item_height_large", BASE_ITEM_HEIGHT_LARGE, 0.8f)
    
    // Get scaled grid item dimensions
    fun getVideoCardHeight(): Int = getScaledDimension("video_card_height", BASE_VIDEO_CARD_HEIGHT, 0.85f)
    fun getAudioItemHeight(): Int = getScaledDimension("audio_item_height", BASE_AUDIO_ITEM_HEIGHT, 0.9f)
    
    // Get number of columns for video grid
    fun getVideoGridColumns(): Int {
        val minColumnWidth = if (displayUtils.screenWidthDp > BREAKPOINT_TABLET) 240f else 180f
        return displayUtils.calculateGridColumns(minColumnWidth)
    }
    
    // Get number of columns for audio grid
    fun getAudioGridColumns(): Int {
        return when {
            displayUtils.screenWidthDp > BREAKPOINT_LARGE_TABLET -> 3
            displayUtils.screenWidthDp > BREAKPOINT_TABLET -> 2
            displayUtils.smallestDimensionDp > BREAKPOINT_PHONE && 
                 displayUtils.screenWidthDp > displayUtils.screenHeightDp -> 2 // Landscape phone
            else -> 1
        }
    }
    
    // Calculate a scaled dimension value based on screen size
    private fun getScaledDimension(key: String, baseDp: Float, tabletScaleFactor: Float): Int {
        // Return cached value if available
        cachedDimensions[key]?.let { return it }
        
        // Calculate scale factor based on device type
        val isTablet = displayUtils.smallestDimensionDp >= BREAKPOINT_TABLET
        val scaleFactor = if (isTablet) tabletScaleFactor else 1.0f
        
        // Calculate the dimension with appropriate scaling
        val result = displayUtils.getProportionalDimension(baseDp * scaleFactor)
        
        // Cache the result
        cachedDimensions[key] = result
        
        return result
    }
    
    // Calculate a scaled text size
    private fun getScaledTextSize(key: String, baseSp: Float, tabletScaleFactor: Float): Float {
        // Calculate the dimension in pixels
        val dimensionPx = getScaledDimension(key, baseSp, tabletScaleFactor)
        
    // Convert back to sp for text sizing
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_PX,
            dimensionPx.toFloat(),
            metrics
        ) / metrics.density
    }
    
    /**
     * Calculate an ideal grid item width based on screen size and column count
     * @param columnCount Number of columns in the grid
     * @param horizontalMarginDp Total horizontal margin (left + right) in dp
     * @return The optimal width in pixels
     */
    fun calculateGridItemWidth(columnCount: Int, horizontalMarginDp: Float = 8f): Int {
        val displayUtils = DisplayUtils.getInstance(context)
        val marginPx = displayUtils.getProportionalDimension(horizontalMarginDp)
        
        // Calculate width based on available space and column count
        val screenWidth = displayUtils.screenWidthPx
        val availableWidth = screenWidth - (2 * marginPx) - (columnCount * 2 * marginPx)
        return availableWidth / columnCount
    }
    
    /**
     * Get responsive dimensions for video grid items
     * @return Pair of (width, height) in pixels, with -1 for MATCH_PARENT
     */    fun getVideoGridItemDimensions(): Pair<Int, Int> {
        val columnCount = getVideoGridColumns()
        
        // For single column layouts, use full width
        val width = if (columnCount <= 1) {
            ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            calculateGridItemWidth(columnCount)
        }
        
        // Calculate proportional height based on 16:9 aspect ratio for videos
        val height = if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
            getVideoCardHeight()
        } else {
            (width * 9) / 16
        }
        
        return Pair(width, height)
    }
    
    /**
     * Get responsive dimensions for audio list items
     * @return Height in pixels
     */
    fun getResponsiveAudioItemHeight(): Int {
        val baseHeight = getAudioItemHeight()
        val displayUtils = DisplayUtils.getInstance(context)
        
        // Make height slightly larger on wider screens
        return when {
            displayUtils.screenWidthDp >= BREAKPOINT_LARGE_TABLET -> (baseHeight * 1.2f).toInt()
            displayUtils.screenWidthDp >= BREAKPOINT_TABLET -> (baseHeight * 1.1f).toInt()
            else -> baseHeight
        }
    }
    
    /**
     * Get the corner radius scaled for current screen size
     * @param baseCornerRadiusDp Base corner radius in dp
     * @return Scaled corner radius in pixels
     */
    fun getScaledCornerRadius(baseCornerRadiusDp: Float = 8f): Int {
        val displayUtils = DisplayUtils.getInstance(context)
        return displayUtils.getProportionalDimension(baseCornerRadiusDp)
    }
}
