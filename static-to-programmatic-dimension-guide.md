# Migrating from Static to Programmatic Dimensions

This guide shows how to migrate your layouts and code from using static dimension resources to the new programmatic, resolution-based dimension system.

## XML Layout Changes

### Before:
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginStart="@dimen/margin_medium"
    android:layout_marginEnd="@dimen/margin_medium"
    android:textSize="@dimen/text_size_large" />
```

### After:
```xml
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_marginStart="8dp"
    android:layout_marginEnd="8dp" />
```

Note: Don't set the textSize in XML - it will be handled programmatically.

## RecyclerView Setup

### Before:
```kotlin
binding.recyclerVideos.apply {
    layoutManager = GridLayoutManager(context, resources.getInteger(R.integer.videos_grid_columns))
    adapter = videosAdapter
}
```

### After:
```kotlin
binding.recyclerVideos.apply {
    // Use the responsive column count from DimensionUtils
    val columns = DimensionUtils.getInstance(requireContext()).getVideoGridColumns()
    layoutManager = GridLayoutManager(context, columns)
    adapter = videosAdapter
    
    // Apply responsive dimensions to the RecyclerView
    ResponsiveUIHelper.getInstance(requireContext()).setupResponsiveRecyclerView(this)
}
```

## ViewHolder Setup

### Before:
```kotlin
class ViewHolder(binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
        // No dimension handling
    }
}
```

### After:
```kotlin
class ViewHolder(binding: ItemVideoBinding) : RecyclerView.ViewHolder(binding.root) {
    init {
        // Apply responsive dimensions to the item
        binding.root.setupAsVideoGridItem()
        
        // Apply adaptive text sizes
        binding.videoTitle.setAdaptiveTextSize(12f, 16f)
        binding.videoDuration.setAdaptiveTextSize(10f, 14f)
        
        // Apply adaptive corner radius
        binding.root.setAdaptiveCornerRadius(8f)
    }
}
```

## Fragment Setup

### Before:
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    // Setup views and adapters
}
```

### After:
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    // Apply responsive dimensions to UI
    ResponsiveUIHelper.getInstance(requireContext())
        .applyResponsiveDimensionsToLayout(binding.root)
    
    // Setup views and adapters
}
```

## Common Dimension Replacements

| Static Dimension | Programmatic Equivalent |
|------------------|-------------------------|
| @dimen/margin_small | 4dp (or dimensionUtils.getMarginSmall()) |
| @dimen/margin_medium | 8dp (or dimensionUtils.getMarginMedium()) |
| @dimen/margin_large | 16dp (or dimensionUtils.getMarginLarge()) |
| @dimen/icon_size_small | 24dp (or dimensionUtils.getIconSizeSmall()) |
| @dimen/icon_size_medium | 32dp (or dimensionUtils.getIconSizeMedium()) |
| @dimen/control_height_medium | 48dp (or dimensionUtils.getControlHeightMedium()) |
| @dimen/card_corner_radius | 8dp (or dimensionUtils.getScaledCornerRadius(8f)) |

## Advantages of Programmatic Dimensions

1. **Adaptability**: Automatically scales to any device resolution
2. **Better Performance**: No need to load different resource files at runtime
3. **Reduced APK Size**: Fewer resource files means smaller app size
4. **More Granular Control**: Can scale elements based on device-specific factors
5. **Future-Proof**: Works with any new device form factor or resolution

## Tips for Conversion

1. Start with RecyclerView items as they appear most frequently
2. Use ViewExtensions for common patterns (list items, grid items, etc.)
3. Apply ResponsiveUIHelper to entire fragment layouts
4. For custom views, use DisplayUtils.getProportionalDimension() 
5. Replace static text sizes with adaptive text sizing
