# XML UI Tuning for Media+ App

This document describes how to use the XML-based UI tuning system to customize the appearance and behavior of the Media+ app.

## Overview

The Media+ app includes an XML-based tuning system that allows for precise customization of UI elements across different screens. These XML files define various properties like sizing, spacing, colors, and animations that can be adjusted without modifying the code.

## Tuning Files

The app includes 5 XML tuning files, each dedicated to a specific screen:

1. **tuning_splash_screen.xml**: For customizing the splash screen
2. **tuning_main_screen.xml**: For customizing the main interface
3. **tuning_video_player.xml**: For customizing the video player
4. **tuning_audio_player.xml**: For customizing the audio player
5. **tuning_search_screen.xml**: For customizing the search screen

These files are located in the `res/values/` directory.

## How to Use

### Basic Usage

To modify any UI parameter, simply edit the corresponding value in the appropriate XML file. For example:

```xml
<!-- Increase the size of album art in the audio player -->
<item name="audio_album_art_size_factor" format="float" type="dimen">1.2</item>

<!-- Change the corner radius of cards in the main screen -->
<dimen name="main_card_corner_radius">12dp</dimen>

<!-- Adjust the splash screen duration -->
<integer name="splash_duration_ms">2000</integer>
```

### Color Customization

For colors, you can use any valid hex color code. For example:

```xml
<!-- Change the splash screen background color to dark blue -->
<color name="splash_background_color">#FF0A1A2E</color>

<!-- Change the title text color in the audio player -->
<color name="audio_title_text_color">#FFEEEEEE</color>
```

If you want to use the default theme colors for an element, set its color value to transparent:

```xml
<color name="search_bar_background_color">#00000000</color>
```

### Measurements

The tuning system supports various measurement units:

- **dp**: Density-independent pixels, for dimensions that should scale with screen density
- **sp**: Scale-independent pixels, for text sizes that should scale with both screen density and user font size settings
- **float**: For scale factors and non-dimension values (typically between 0.0 and 2.0)
- **integer**: For milliseconds, durations, and other integer values

## Available Parameters

Each screen has its own set of parameters that can be tuned. Here's a brief overview:

### Splash Screen Parameters

- Duration, logo scale, animation speed
- Colors, opacity, animation characteristics
- Logo positioning

### Main Screen Parameters

- Navigation bar sizing
- Text sizes for section titles
- Spacing between content items
- Card corner radius
- Transition animations
- Padding and margin adjustments
- Color customizations

### Video Player Parameters

- Control size and spacing
- Seek bar appearance
- Control overlay opacity
- Animation durations
- Padding adjustments
- Color customizations
- Gesture sensitivity

### Audio Player Parameters

- Album art size and appearance
- Control size and spacing
- Text sizing for titles, artists, and timestamps
- Design elements (corner radius, elevation)
- Color customizations
- Transition animations
- Visualizer sensitivity

### Search Screen Parameters

- Search bar appearance
- Text sizing
- Animation durations
- Padding and margin adjustments
- Color customizations
- Result item spacing and appearance

## Implementation

The app uses the `TuningProvider` class to access these XML values. This provider retrieves the values from resources and makes them available throughout the app. You typically won't need to modify this class unless you're adding new tuning parameters.

## Best Practices

1. **Test on multiple devices**: After making changes, test the app on different screen sizes to ensure your customizations work well across all devices.

2. **Maintain proportions**: When adjusting size factors, try to maintain proportional relationships between elements.

3. **Color harmony**: When customizing colors, ensure they maintain good contrast and accessibility.

4. **Animation timing**: Keep animations snappy - values between 200-400ms are typically best for most transitions.

5. **Restore defaults**: If something doesn't look right, you can restore default values by copying them from this documentation.
