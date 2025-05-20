package com.mediaplus.app.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.mediaplus.app.R

/**
 * Helper class to apply responsive dimensions to views within the app.
 * This class can be used to dynamically resize UI elements based on screen metrics.
 */
class ResponsiveUIHelper(private val context: Context) {
    
    private val dimensionUtils = DimensionUtils.getInstance(context)
    private val displayUtils = DisplayUtils.getInstance(context)
    
    /**
     * Apply responsive dimensions to a view based on its ID or type
     * @param view The view to apply dimensions to
     */
    fun applyResponsiveDimensions(view: View) {
        when (view) {
            is ViewGroup -> applyToViewGroup(view)
            is TextView -> applyToTextView(view)
            is ImageView -> applyToImageView(view)
            is MaterialCardView -> applyToCardView(view)
        }
    }
    
    /**
     * Apply responsive dimensions to all views in a layout recursively
     * @param rootView The root view of the layout
     */
    fun applyToLayout(rootView: View) {
        applyResponsiveDimensions(rootView)
        
        if (rootView is ViewGroup) {
            rootView.children.forEach { childView ->
                applyToLayout(childView)
            }
        }
    }
    
    /**
     * Apply responsive dimensions to a specific fragment or activity layout
     * @param rootView The root view of the fragment or activity
     * @param containerIds Optional array of container view IDs to focus on
     */
    fun applyResponsiveDimensionsToLayout(rootView: View, vararg containerIds: Int) {
        if (containerIds.isEmpty()) {
            applyToLayout(rootView)
            return
        }
        
        // Apply to specific containers if specified
        containerIds.forEach { containerId ->
            rootView.findViewById<View>(containerId)?.let { container ->
                applyToLayout(container)
            }
        }
    }
    
    /**
     * Applies responsive dimensions to a specific RecyclerView and its adapter items
     * @param recyclerView The RecyclerView to apply dimensions to
     * @param columnCount The number of columns to use (-1 for auto-calculation)
     * @param orientation The orientation of the RecyclerView layout
     */
    fun setupResponsiveRecyclerView(
        recyclerView: RecyclerView,
        columnCount: Int = -1,
        orientation: Int = RecyclerView.VERTICAL
    ) {
        // Calculate appropriate column count based on screen size if not specified
        val columns = when {
            columnCount > 0 -> columnCount
            recyclerView.id == R.id.recycler_videos -> dimensionUtils.getVideoGridColumns()
            recyclerView.id == R.id.recycler_music -> dimensionUtils.getAudioGridColumns()
            else -> {
                val minColumnWidth = if (displayUtils.screenWidthDp >= 600f) 200f else 160f
                displayUtils.calculateGridColumns(minColumnWidth)
            }
        }
        
        // Setup appropriate layout manager
        val layoutManager = when (orientation) {
            RecyclerView.HORIZONTAL -> {
                androidx.recyclerview.widget.LinearLayoutManager(
                    recyclerView.context, 
                    RecyclerView.HORIZONTAL, 
                    false
                )
            }
            else -> {
                if (columns > 1) {
                    val gridLayoutManager = androidx.recyclerview.widget.GridLayoutManager(
                        recyclerView.context, 
                        columns, 
                        orientation, 
                        false
                    )
                      // Optional: handle different span sizes for different items
                    recyclerView.adapter?.let { _ ->
                        // If you want to set a custom SpanSizeLookup, do it directly on gridLayoutManager
                        // Example: gridLayoutManager.spanSizeLookup = customSpanSizeLookup
                    }
                    
                    gridLayoutManager
                } else {
                    androidx.recyclerview.widget.LinearLayoutManager(
                        recyclerView.context, 
                        orientation, 
                        false
                    )
                }
            }
        }
        
        recyclerView.layoutManager = layoutManager
        
        // Apply responsive padding
        val padding = dimensionUtils.getMarginMedium()
        recyclerView.setPadding(padding, padding, padding, padding)
    }
    
    private fun applyToViewGroup(viewGroup: ViewGroup) {
        when (viewGroup) {
            is RecyclerView -> applyToRecyclerView(viewGroup)
            else -> {
                // Apply margins and padding
                val layoutParams = viewGroup.layoutParams as? ViewGroup.MarginLayoutParams
                layoutParams?.let {
                    when (viewGroup.id) {
                        R.id.controls_scroll_container, R.id.layout_controls -> {
                            it.topMargin = dimensionUtils.getMarginXLarge()
                        }
                    }
                }
                
                when (viewGroup.id) {
                    R.id.layout_controls -> {
                        viewGroup.setPadding(
                            dimensionUtils.getMarginMedium(),
                            0,
                            dimensionUtils.getMarginMedium(),
                            0
                        )
                    }
                }
            }
        }
    }
    
    private fun applyToRecyclerView(recyclerView: RecyclerView) {
        // Configure RecyclerView grid/list layout based on screen size
        when (recyclerView.id) {
            R.id.recycler_videos -> {
                recyclerView.setupResponsiveGrid(dimensionUtils.getVideoGridColumns())
                recyclerView.setPadding(
                    dimensionUtils.getMarginMedium(),
                    dimensionUtils.getMarginMedium(),
                    dimensionUtils.getMarginMedium(),
                    dimensionUtils.getMarginMedium()
                )
            }
            R.id.recycler_music -> {
                recyclerView.setupResponsiveGrid(dimensionUtils.getAudioGridColumns())
                recyclerView.setPadding(
                    dimensionUtils.getMarginMedium(),
                    dimensionUtils.getMarginMedium(),
                    dimensionUtils.getMarginMedium(),
                    dimensionUtils.getMarginMedium()
                )
            }
        }
    }
    
    private fun applyToTextView(textView: TextView) {
        // Apply responsive text sizes based on text view ID or style
        when (textView.id) {
            R.id.title_videos, R.id.title_music, R.id.title_playlists -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeHeading())
                
                // Apply appropriate margins
                (textView.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                    it.setMargins(
                        dimensionUtils.getMarginLarge(),
                        dimensionUtils.getMarginLarge(),
                        dimensionUtils.getMarginMedium(),
                        0
                    )
                }
            }
            R.id.video_title, R.id.media_title -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeMedium())
                
                // Apply appropriate margins
                (textView.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                    it.setMargins(
                        dimensionUtils.getMarginMedium(),
                        0,
                        dimensionUtils.getMarginMedium(),
                        dimensionUtils.getMarginSmall()
                    )
                }
            }
            R.id.video_duration, R.id.media_subtitle -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeSmall())
                
                // Apply appropriate margins
                (textView.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                    it.setMargins(
                        dimensionUtils.getMarginMedium(),
                        0,
                        dimensionUtils.getMarginMedium(),
                        dimensionUtils.getMarginMedium()
                    )
                }
            }
            R.id.txt_title -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeXXLarge())
                
                // Apply appropriate margins
                (textView.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                    it.setMargins(
                        dimensionUtils.getMarginXLarge(),
                        dimensionUtils.getMarginXLarge(),
                        dimensionUtils.getMarginXLarge(),
                        0
                    )
                }
            }
            R.id.txt_artist -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeLarge())
                
                // Apply appropriate margins
                (textView.layoutParams as? ViewGroup.MarginLayoutParams)?.let {
                    it.setMargins(
                        dimensionUtils.getMarginXLarge(),
                        dimensionUtils.getMarginMedium(),
                        dimensionUtils.getMarginXLarge(),
                        0
                    )
                }
            }
            R.id.txt_current_time, R.id.txt_total_time -> {
                textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, dimensionUtils.getTextSizeSmall())
            }
        }
    }
      private fun applyToImageView(imageView: ImageView) {
        // Apply responsive sizes to image views
        when (imageView.id) {
            R.id.btn_sort_videos, R.id.btn_sort_music -> {
                imageView.layoutParams.width = getControlHeightMedium()
                imageView.layoutParams.height = getControlHeightMedium()
            }
            R.id.btn_play, R.id.btn_next, R.id.btn_previous, 
            R.id.btn_repeat, R.id.btn_shuffle, R.id.btn_equalizer, R.id.btn_lyrics -> {
                // These are already handled in our extensions
                imageView.layoutParams.width = getPlayerControlSize()
                imageView.layoutParams.height = getPlayerControlSize()
            }
        }
    }
      private fun applyToCardView(cardView: MaterialCardView) {
        // Apply responsive dimensions to card views
        when (cardView.id) {
            R.id.card_album_art -> {
                // Album art in audio player
                val layoutParams = cardView.layoutParams
                layoutParams.width = 0 // Use constraint layout width percent
                
                // Update corner radius
                cardView.radius = getCornerRadiusLarge().toFloat()
            }
        }
    }
    
    companion object {
        // Singleton instance
        @Volatile
        private var INSTANCE: ResponsiveUIHelper? = null
        
        fun getInstance(context: Context): ResponsiveUIHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ResponsiveUIHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Additional helper methods to get various responsive dimensions
    fun getCornerRadiusSmall(): Int = displayUtils.getProportionalDimension(4f)
    fun getCornerRadiusMedium(): Int = displayUtils.getProportionalDimension(8f)
    fun getControlHeightMedium(): Int = dimensionUtils.getItemHeightMedium()
    fun getControlHeightSmall(): Int = dimensionUtils.getItemHeightSmall()
    fun getPlayerControlSize(): Int = dimensionUtils.getIconSizeLarge()
    fun getCornerRadiusLarge(): Int = dimensionUtils.getScaledCornerRadius(12f)
}
