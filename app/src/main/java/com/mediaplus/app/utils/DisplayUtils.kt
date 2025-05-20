package com.mediaplus.app.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Utility class to handle responsive UI calculations based on device screen metrics.
 * This allows for adaptive layouts that scale properly on any screen size or density.
 */
class DisplayUtils(private val context: Context) {
    
    // Screen metrics
    private val displayMetrics: DisplayMetrics = context.resources.displayMetrics
    private val density: Float = displayMetrics.density
    
    // Screen dimensions in pixels
    val screenWidthPx: Int = displayMetrics.widthPixels
    val screenHeightPx: Int = displayMetrics.heightPixels
    
    // Screen dimensions in dp
    val screenWidthDp: Float = screenWidthPx / density
    val screenHeightDp: Float = screenHeightPx / density
    
    // Smallest screen dimension (used for responsive calculations)
    val smallestDimensionDp: Float = min(screenWidthDp, screenHeightDp)
    
    // Reference size used for calculations (based on a typical phone)
    private val baseScreenWidth = 375f // iPhone 8 width in dp as reference
    private val baseScreenHeight = 667f // iPhone 8 height in dp as reference
    
    // Scaling factors
    val widthScaleFactor: Float = screenWidthDp / baseScreenWidth
    val heightScaleFactor: Float = screenHeightDp / baseScreenHeight
    
    /**
     * Get a dimension scaled based on the device's width compared to the base width
     * @param baseDp The base dimension in dp for a reference device
     * @return The scaled dimension in pixels
     */
    fun getWidthScaledDimension(baseDp: Float): Int {
        return (baseDp * widthScaleFactor * density).roundToInt()
    }
    
    /**
     * Get a dimension scaled based on the device's height compared to the base height
     * @param baseDp The base dimension in dp for a reference device
     * @return The scaled dimension in pixels
     */
    fun getHeightScaledDimension(baseDp: Float): Int {
        return (baseDp * heightScaleFactor * density).roundToInt()
    }
    
    /**
     * Get a dimension that's scaled proportionally to the smaller of width or height
     * scaling factors. This prevents overly large dimensions on tablets.
     * @param baseDp The base dimension in dp for a reference device
     * @return The scaled dimension in pixels
     */
    fun getProportionalDimension(baseDp: Float): Int {
        val scaleFactor = min(widthScaleFactor, heightScaleFactor)
        return (baseDp * scaleFactor * density).roundToInt()
    }
    
    /**
     * Calculate the appropriate number of grid columns based on screen width and
     * a target minimum column width in dp
     * @param minColumnWidthDp Minimum width for each column in dp
     * @return The number of columns that can fit on screen
     */
    fun calculateGridColumns(minColumnWidthDp: Float): Int {
        val columns = (screenWidthDp / minColumnWidthDp).toInt()
        return maxOf(1, columns) // Ensure at least 1 column
    }
    
    /**
     * Calculate the width of each item in a grid layout
     * @param columnCount The number of columns
     * @param horizontalMarginDp The total horizontal margin (left + right) in dp
     * @return The item width in pixels
     */
    fun calculateGridItemWidth(columnCount: Int, horizontalMarginDp: Float): Int {
        val availableWidth = screenWidthPx - (horizontalMarginDp * density).roundToInt()
        return availableWidth / columnCount
    }
    
    /**
     * Get a dimension that's proportionally scaled based on a target device width
     * @param baseDp The base dimension in dp for a reference device
     * @param targetDeviceWidthDp The target device width in dp (default 360dp)
     * @return The scaled dimension in pixels
     */
    fun getScaledDimensionForWidth(baseDp: Float, targetDeviceWidthDp: Float = 360f): Int {
        val scaleFactor = screenWidthDp / targetDeviceWidthDp
        val cappedScaleFactor = scaleFactor.coerceIn(0.75f, 1.5f) // Limit scaling range
        return (baseDp * cappedScaleFactor * density).roundToInt()
    }
    
    /**
     * Calculate the ideal column count for a grid layout based on actual item width
     * @param idealColumnCountPhone Ideal column count on phone screens
     * @param idealColumnCountTablet Ideal column count on tablet screens
     * @return The optimal column count for current device
     */
    fun getOptimalColumnCount(idealColumnCountPhone: Int = 2, idealColumnCountTablet: Int = 3): Int {
        return when {
            smallestDimensionDp >= LARGE_TABLET -> idealColumnCountTablet + 1
            smallestDimensionDp >= SMALL_TABLET -> idealColumnCountTablet
            else -> idealColumnCountPhone
        }
    }
    
    /**
     * Get a scaling factor that varies based on device form factor
     * @return A scaling factor between 0.8 and 1.2
     */
    fun getDeviceFormFactorScaling(): Float {
        val aspectRatio = screenWidthDp / screenHeightDp
        
        // Devices with unusual aspect ratios get special treatment
        return when {
            aspectRatio > 0.65f -> 1.1f  // More square-like devices
            aspectRatio < 0.45f -> 0.9f  // Extra tall/narrow devices
            smallestDimensionDp > SMALL_TABLET -> 1.2f // Tablets get larger elements
            else -> 1.0f
        }
    }
    
    /**
     * Get a screen density-aware dimension
     * This is especially useful for borders and fine details
     * @param baseDp The base dimension in dp
     * @return The scaled dimension in pixels, adjusted for screen density
     */
    fun getDensityAwareDimension(baseDp: Float): Int {
        val densityFactor = when {
            density >= 3.5f -> 1.3f  // xxxhdpi
            density >= 3.0f -> 1.2f  // xxhdpi
            density >= 2.0f -> 1.0f  // xhdpi
            density >= 1.5f -> 0.9f  // hdpi
            else -> 0.8f            // mdpi and below
        }
        
        return (baseDp * densityFactor * density).roundToInt()
    }
    
    companion object {
        // Screen size categories based on smallest dimension in dp
        const val SMALL_PHONE = 320f
        const val NORMAL_PHONE = 400f
        const val LARGE_PHONE = 480f
        const val SMALL_TABLET = 600f
        const val LARGE_TABLET = 800f
        
        // Device categories
        const val CATEGORY_SMALL_PHONE = 0
        const val CATEGORY_NORMAL_PHONE = 1
        const val CATEGORY_LARGE_PHONE = 2
        const val CATEGORY_SMALL_TABLET = 3
        const val CATEGORY_LARGE_TABLET = 4
        
        /**
         * Factory method to create an instance
         */
        fun getInstance(context: Context): DisplayUtils {
            return DisplayUtils(context)
        }
    }
    
    /**
     * Get the device category based on screen size
     * @return The device category as an integer constant
     */
    fun getDeviceCategory(): Int {
        return when {
            smallestDimensionDp < NORMAL_PHONE -> CATEGORY_SMALL_PHONE
            smallestDimensionDp < LARGE_PHONE -> CATEGORY_NORMAL_PHONE
            smallestDimensionDp < SMALL_TABLET -> CATEGORY_LARGE_PHONE
            smallestDimensionDp < LARGE_TABLET -> CATEGORY_SMALL_TABLET
            else -> CATEGORY_LARGE_TABLET
        }
    }
    
    /**
     * Get a value from an array based on the device category
     * @param values Array of values corresponding to device categories
     * @param defaultValue Default value to return if array doesn't have enough values
     * @return The appropriate value for the current device category
     */
    fun <T> getValueForDeviceCategory(values: Array<T>, defaultValue: T): T {
        val category = getDeviceCategory()
        return if (category < values.size) values[category] else defaultValue
    }
}
