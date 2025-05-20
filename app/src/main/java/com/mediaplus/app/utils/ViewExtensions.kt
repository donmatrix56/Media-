package com.mediaplus.app.utils

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Extension functions to apply responsive dimensions to views
 */

/**
 * Set width and height for a view based on device screen metrics
 * @param widthDp Base width in dp
 * @param heightDp Base height in dp
 */
fun View.setResponsiveSize(widthDp: Float, heightDp: Float) {
    val displayUtils = DisplayUtils.getInstance(context)
    
    val width = if (widthDp > 0) displayUtils.getProportionalDimension(widthDp) else
        ViewGroup.LayoutParams.WRAP_CONTENT
    
    val height = if (heightDp > 0) displayUtils.getProportionalDimension(heightDp) else
        ViewGroup.LayoutParams.WRAP_CONTENT
    
    layoutParams = layoutParams.apply {
        this.width = width
        this.height = height
    }
}

/**
 * Set margins for a view based on device screen metrics
 * @param leftDp Left margin in dp
 * @param topDp Top margin in dp
 * @param rightDp Right margin in dp
 * @param bottomDp Bottom margin in dp
 */
fun View.setResponsiveMargins(leftDp: Float = 0f, topDp: Float = 0f, rightDp: Float = 0f, bottomDp: Float = 0f) {
    val displayUtils = DisplayUtils.getInstance(context)
    
    val leftPx = displayUtils.getProportionalDimension(leftDp)
    val topPx = displayUtils.getProportionalDimension(topDp)
    val rightPx = displayUtils.getProportionalDimension(rightDp)
    val bottomPx = displayUtils.getProportionalDimension(bottomDp)
    
    (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        setMargins(leftPx, topPx, rightPx, bottomPx)
    }
}

/**
 * Set padding for a view based on device screen metrics
 * @param leftDp Left padding in dp
 * @param topDp Top padding in dp
 * @param rightDp Right padding in dp
 * @param bottomDp Bottom padding in dp
 */
fun View.setResponsivePadding(leftDp: Float = 0f, topDp: Float = 0f, rightDp: Float = 0f, bottomDp: Float = 0f) {
    val displayUtils = DisplayUtils.getInstance(context)
    
    val leftPx = displayUtils.getProportionalDimension(leftDp)
    val topPx = displayUtils.getProportionalDimension(topDp)
    val rightPx = displayUtils.getProportionalDimension(rightDp)
    val bottomPx = displayUtils.getProportionalDimension(bottomDp)
    
    setPadding(leftPx, topPx, rightPx, bottomPx)
}

/**
 * Set text size based on device screen metrics
 * @param sizeSp Base text size in sp
 */
fun TextView.setResponsiveTextSize(sizeSp: Float) {
    val displayUtils = DisplayUtils.getInstance(context)
    val scaleFactor = kotlin.math.min(displayUtils.widthScaleFactor, displayUtils.heightScaleFactor)
    setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, sizeSp * scaleFactor)
}

/**
 * Configure a RecyclerView with a GridLayoutManager based on screen size
 * @param columnCount The number of columns for the grid
 * @param orientation The orientation of the layout
 */
fun RecyclerView.setupResponsiveGrid(
    columnCount: Int = 0,
    orientation: Int = RecyclerView.VERTICAL
) {
    val dimensionUtils = DimensionUtils.getInstance(context)
    val columns = columnCount.takeIf { it > 0 } ?: dimensionUtils.getVideoGridColumns()
    layoutManager = GridLayoutManager(context, columns, orientation, false)
}

/**
 * Configure margins for a video grid item
 */
fun View.setupAsVideoGridItem() {
    val dimensionUtils = DimensionUtils.getInstance(context)
    val margin = dimensionUtils.getMarginSmall()
    
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        height = dimensionUtils.getVideoCardHeight()
        setMargins(margin, margin, margin, margin)
    } ?: ViewGroup.MarginLayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        dimensionUtils.getVideoCardHeight()
    ).apply {
        setMargins(margin, margin, margin, margin)
    }
}

/**
 * Configure margins for an audio list item
 */
fun View.setupAsAudioListItem() {
    val dimensionUtils = DimensionUtils.getInstance(context)
    val margin = dimensionUtils.getMarginSmall()
    
    layoutParams = (layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        height = dimensionUtils.getAudioItemHeight()
        setMargins(margin, margin, margin, margin)
    } ?: ViewGroup.MarginLayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        dimensionUtils.getAudioItemHeight()
    ).apply {
        setMargins(margin, margin, margin, margin)
    }
}

/**
 * Set icon size based on device screen metrics
 * @param sizeDp Base icon size in dp
 */
fun ImageView.setResponsiveIconSize(sizeDp: Float) {
    val displayUtils = DisplayUtils.getInstance(context)
    val size = displayUtils.getProportionalDimension(sizeDp)
    
    layoutParams = layoutParams.apply {
        width = size
        height = size
    }
}

/**
 * Apply responsive dimensions to RecyclerView items flexibly
 * @param itemWidthDp Base width in dp (-1 for MATCH_PARENT)
 * @param itemHeightDp Base height in dp (-1 for WRAP_CONTENT)
 * @param marginDp Margin to apply to all sides in dp
 */
fun RecyclerView.setupResponsiveItems(itemWidthDp: Float = -1f, itemHeightDp: Float = -1f, marginDp: Float = 4f) {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: android.graphics.Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val displayUtils = DisplayUtils.getInstance(context)
            val margin = displayUtils.getProportionalDimension(marginDp)
            outRect.set(margin, margin, margin, margin)
            
            // Apply responsive dimensions to item view
            if (itemWidthDp > 0 || itemHeightDp > 0) {
                view.setResponsiveSize(itemWidthDp, itemHeightDp)
            }
        }
    })
}

/**
 * Apply responsive text size with adaptive scaling based on screen width
 * @param minSp Minimum text size in sp
 * @param maxSp Maximum text size in sp
 */
fun TextView.setAdaptiveTextSize(minSp: Float, maxSp: Float) {
    val displayUtils = DisplayUtils.getInstance(context)
    val screenSizeMultiplier = (displayUtils.screenWidthDp / 360f).coerceIn(0.75f, 1.5f)
    val adaptiveSize = minSp + (maxSp - minSp) * screenSizeMultiplier
    
    setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, adaptiveSize)
}

/**
 * Configures a view to have adaptive corner radius based on screen size
 * @param radiusDp Base corner radius in dp
 */
fun View.setAdaptiveCornerRadius(radiusDp: Float) {
    val displayUtils = DisplayUtils.getInstance(context)
    val radius = displayUtils.getProportionalDimension(radiusDp)
    
    if (this is com.google.android.material.card.MaterialCardView) {
        this.radius = radius.toFloat()
    } else {
        val drawable = background
        if (drawable is android.graphics.drawable.GradientDrawable) {
            drawable.cornerRadius = radius.toFloat()
        }
    }
}

/**
 * Configure a media card with aspect ratio from settings
 * This applies the home_card_aspect_ratio to determine the height based on width
 */
fun View.setupWithCardAspectRatio() {
    val tuning = TuningProvider.getInstance(context)
    val aspectRatio = tuning.getHomeCardAspectRatio()
    
    // Get the current width
    post {
        val width = width
        if (width > 0) {
            // Calculate height based on aspect ratio (height = width * aspectRatio)
            val height = (width * aspectRatio).toInt()
            
            // Apply the calculated height
            layoutParams = layoutParams.apply {
                this.height = height
            }
        }
    }
}
